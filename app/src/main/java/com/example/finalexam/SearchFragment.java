package com.example.finalexam;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
Pramukh Nagendra
id: 801167475
FileName: FinalExam_Pramukh
 */

public class SearchFragment extends Fragment {

    private final OkHttpClient client = new OkHttpClient();
    static AlbumListAdapter albumListAdapter;
    ArrayList<Album> albumsList = new ArrayList<>();
    ArrayList<Album> likedAlbumsList = new ArrayList<>();
    RecyclerView albumRecyclerView;
    LinearLayoutManager layoutManager;
    EditText artistEditText;
    Button searchButton;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Search");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        artistEditText = view.findViewById(R.id.artistEditText);
        searchButton = view.findViewById(R.id.searchButton);

        albumRecyclerView = view.findViewById(R.id.albumRecyclerView);
        albumRecyclerView.setHasFixedSize(true);
        albumListAdapter = new AlbumListAdapter(albumsList, likedAlbumsList,getContext(), new AlbumListAdapter.IAlbumListAdapter() {
            @Override
            public void callAlbumActivity(Album album) {
                mListener.callDetailAlbumFragment(album);
            }

            @Override
            public void likesDislikeFunctionality(Album album) {

                for (Album alb : likedAlbumsList) {
                    if (alb.id == album.id) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection("liked")
                                .document(String.valueOf(album.id)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getLikesDetails();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                        return;
                    }
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                HashMap<String, Object> map = new HashMap<>();
                map.put("id",album.id);
                map.put("title", album.albumTitle);
                map.put("artistName", album.artistName);
                map.put("artistImage",album.artistCoverImage);
                map.put("image",album.albumCoverImage);
                map.put("nb_tracks",album.nb_tracks);
                db.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("liked").document(String.valueOf(album.id)).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getLikesDetails();
                    }
                });

            }
        });
        layoutManager = new LinearLayoutManager(getContext());
        albumRecyclerView.setLayoutManager(layoutManager);
        albumRecyclerView.setAdapter(albumListAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = artistEditText.getText().toString();

                if(searchText.length() == 0){
                    AlertUtils.showOKDialog(getActivity(),getResources().getString(R.string.error),
                            getResources().getString(R.string.enter_artist));
                }else {
                    getAlbumDetails(searchText);
                }
            }
        });

        return view;
    }


    public void getLikesDetails(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("liked")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        likedAlbumsList.clear();
                        for(QueryDocumentSnapshot ds: value){
                            Album album = new Album();
                            album.id = (long)ds.getData().get("id");
                            album.albumTitle = (String) ds.getData().get("title");
                            album.artistName = (String) ds.getData().get("artistName");
                            album.nb_tracks = (long) ds.getData().get("nb_tracks");
                            album.albumCoverImage = (String) ds.getData().get("image");
                            album.timestamp = (Timestamp) ds.getData().get("createdAt");
                            likedAlbumsList.add(album);
                        }
                        albumListAdapter.notifyDataSetChanged();
                    }
                });
    }


    ISearchFragListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (ISearchFragListener) context;
    }

    public interface ISearchFragListener{
        void callDetailAlbumFragment(Album album);
    }


    public void getAlbumDetails(String artistName){

        HttpUrl url = HttpUrl.parse("https://api.deezer.com/search/album?").newBuilder()
                .addQueryParameter("q", artistName)
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
                        albumsList.clear();
                        for(int i = 0; i< jsonArray.length(); i++){
                            JSONObject albumJSONObject = jsonArray.getJSONObject(i);
                            Album album = new Album(albumJSONObject);
                            albumsList.add(album);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                albumListAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}