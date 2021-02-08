package com.example.finalexam;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlbumSharingFragment extends Fragment {

    ListView shareUserListView;
    TextView albumTitleShareTV;
    ArrayAdapter<String> sharingListAdapter;
    ArrayList<String> userNamesList = new ArrayList<>();
    private FirebaseAuth mAuth;
    HashMap<String,String> map;
    Album album;

    public AlbumSharingFragment(Album album) {
        this.album = album;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Album Sharing");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album_sharing, container, false);

        albumTitleShareTV = view.findViewById(R.id.albumTitleShareTV);
        shareUserListView = view.findViewById(R.id.shareUserListView);
        sharingListAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, android.R.id.text1, userNamesList);
        shareUserListView.setAdapter(sharingListAdapter);

        albumTitleShareTV.setText(album.albumTitle);

        shareUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sharedUserID = map.get(userNamesList.get(position));
                addToSharedList(sharedUserID, userNamesList.get(position));
                mListener.popAlbumSharingFrag();
            }
        });


        getUserNames();


        return view;
    }


    public void addToSharedList(String sharedUserID, String username){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> sharedMap = new HashMap<>();

        sharedMap.put("albumID",album.id);
        sharedMap.put("albumTitle",album.albumTitle);
        sharedMap.put("nb_tracks",album.nb_tracks);
        sharedMap.put("image",album.albumCoverImage);
        sharedMap.put("artistImage",album.artistCoverImage);
        sharedMap.put("artistName",album.artistName);
        sharedMap.put("sharedUsername",username);
        sharedMap.put("sharedTime", new Timestamp(new Date()));

        db.collection("users")
                .document(sharedUserID)
                .collection("shared")
                .document(String.valueOf(album.id)).set(sharedMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }


    public void getUserNames(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        map = new HashMap<>();
        userNamesList.clear();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        if(!documentSnapshot.getId().equals(mAuth.getCurrentUser().getUid())){
                            map.put(String.valueOf(documentSnapshot.getData().get("username")) ,documentSnapshot.getId());
                            userNamesList.add(String.valueOf(documentSnapshot.getData().get("username")));
                        }
                    }
                    sharingListAdapter.notifyDataSetChanged();
                }else{

                }
            }
        });

    }

    IAlbumSharingListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (IAlbumSharingListener) context;
    }

    public interface IAlbumSharingListener{
        void popAlbumSharingFrag();
    }

}