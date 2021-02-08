package com.example.finalexam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class LikesFragment extends Fragment {

    RecyclerView likesRecyclerView;
    FirebaseAuth mAuth;
    ArrayList<Album> likedAlbumsList = new ArrayList<>();
    static AlbumListAdapter likesAdapter;
    final static public String ALBUM_KEY = "ALBUM";

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Likes");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        getLikesDetails();
        likesRecyclerView = view.findViewById(R.id.likesRecyclerView);
        likesRecyclerView.setHasFixedSize(true);
        likesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        likesAdapter = new AlbumListAdapter(likedAlbumsList, likedAlbumsList, getContext(), new AlbumListAdapter.IAlbumListAdapter() {
            @Override
            public void callAlbumActivity(Album album) {
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
        likesRecyclerView.setAdapter(likesAdapter);

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
                            album.id = (long) ds.getData().get("id");
                            album.albumTitle = (String) ds.getData().get("title");
                            album.artistName = (String) ds.getData().get("artistName");
                            album.nb_tracks = (long) ds.getData().get("nb_tracks");
                            album.albumCoverImage = (String) ds.getData().get("image");
                            album.timestamp = (Timestamp) ds.getData().get("createdAt");
                            likedAlbumsList.add(album);
                        }
                        likesAdapter.notifyDataSetChanged();
                    }
                });
    }


}