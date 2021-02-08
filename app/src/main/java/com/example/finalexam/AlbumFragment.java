package com.example.finalexam;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlbumFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    public static Album albumDetails;
    TextView detailsAlbumTitleTV, detailArtistNameTV, detailsNbTracksTV;
    ImageView detailsAlbumCoverIV, detailsArtistIV, shareButtonIV;
    RecyclerView tracksListRecyclerView;
    TrackListHorAdapter trackListHorAdapter;
    LinearLayoutManager layoutManager;
    ArrayList<Tracks> tracksList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Album");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        albumDetails = mListener.getAlbumDetails();

        detailsAlbumTitleTV = view.findViewById(R.id.detailsAlbumTitleTV);
        detailArtistNameTV = view.findViewById(R.id.detailArtistNameTV);
        detailsNbTracksTV = view.findViewById(R.id.detailsNbTracksTV);
        detailsAlbumCoverIV = view.findViewById(R.id.detailsAlbumCoverIV);
        detailsArtistIV = view.findViewById(R.id.detailsArtistIV);
        shareButtonIV = view.findViewById(R.id.shareButtonIV);

        detailsAlbumTitleTV.setText(albumDetails.albumTitle);
        detailArtistNameTV.setText(albumDetails.artistName);
        detailsNbTracksTV.setText(getResources().getString(R.string.track_list)+" ("+albumDetails.nb_tracks+")");
        String albumImageUrl = albumDetails.albumCoverImage;
        Picasso.get().load(albumImageUrl).into(detailsAlbumCoverIV);

        String artistImageUrl = albumDetails.artistCoverImage;
        Picasso.get().load(artistImageUrl).into(detailsArtistIV);

        tracksListRecyclerView = view.findViewById(R.id.tracksListRecyclerView);
        tracksListRecyclerView.setHasFixedSize(true);
        trackListHorAdapter = new TrackListHorAdapter(tracksList, albumDetails, getContext(), new TrackListHorAdapter.ITrackListListener() {
            @Override
            public void updateHistoryList(Tracks tracks) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                HashMap<String,Object> map = new HashMap<>();
                map.put("trackTitle",tracks.trackTitle);
                map.put("albumTitle", albumDetails.albumTitle);
                map.put("artistName", albumDetails.artistName);
                map.put("trackId", tracks.trackId);
                map.put("trackPlayedAt", new Timestamp(new Date()));
                map.put("albumCoverMedium", albumDetails.albumCoverImage);
                db.collection("users").document(mAuth.getCurrentUser().getUid())
                        .collection("history").document(String.valueOf(tracks.trackId)).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }
        });
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        tracksListRecyclerView.setLayoutManager(layoutManager);
        tracksListRecyclerView.setAdapter(trackListHorAdapter);

        getTracksDetails(albumDetails.trackList);

        shareButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callAlbumSharingFragment(albumDetails);
            }
        });

        return view;
    }

    public void getTracksDetails(String trackList){

        HttpUrl url = HttpUrl.parse(trackList).newBuilder()
                .build();


        Request request = new Request.Builder().url(String.valueOf(url)).build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        tracksList.clear();
                        for(int i = 0; i< jsonArray.length(); i++){
                            JSONObject albumJSONObject = jsonArray.getJSONObject(i);
                            Tracks tracks = new Tracks(albumJSONObject, albumDetails.albumTitle, albumDetails.albumCoverImage);
                            tracksList.add(tracks);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trackListHorAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    IAlbumFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IAlbumFragmentListener) context;
    }

    public interface IAlbumFragmentListener{
        Album getAlbumDetails();
        void callAlbumSharingFragment(Album album);
    }

}