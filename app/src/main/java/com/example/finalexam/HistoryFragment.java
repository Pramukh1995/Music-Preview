package com.example.finalexam;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    Button clearHistoryButton;
    RecyclerView historyListRecyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<Tracks> historyArrayList = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    HistoryListAdapter historyAdapter;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("History");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        clearHistoryButton = view.findViewById(R.id.clearHistoryButton);

        historyListRecyclerView = view.findViewById(R.id.historyListRecyclerView);
        historyListRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        getTrackHistoryDetails();

        historyListRecyclerView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryListAdapter(historyArrayList);
        historyListRecyclerView.setAdapter(historyAdapter);

        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                for(Tracks tracks : historyArrayList){
                    db.collection("users").document(mAuth.getCurrentUser().getUid())
                            .collection("history").document(String.valueOf(tracks.trackId)).delete();
                }
                historyArrayList.clear();
            }
        });

        return view;
    }


    public void getTrackHistoryDetails(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("history").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                historyArrayList.clear();
                for (QueryDocumentSnapshot documentSnapshot: value) {
                    Tracks tracks = new Tracks();
                    tracks.albumTitle = (String) documentSnapshot.get("albumTitle");
                    tracks.trackTitle = (String) documentSnapshot.get("artistName");
                    tracks.trackId = (long) documentSnapshot.get("trackId");
                    tracks.albumCoverImage = (String) documentSnapshot.get("albumCoverMedium");
                    tracks.timestamp = (com.google.firebase.Timestamp) documentSnapshot.getData().get("trackPlayedAt");
                    historyArrayList.add(tracks);
                }
                historyAdapter.notifyDataSetChanged();
            }
        });
    }


}