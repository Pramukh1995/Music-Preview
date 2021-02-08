package com.example.finalexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder>{

    ArrayList<Tracks> historyList;

    public HistoryListAdapter(ArrayList<Tracks> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tracks tracks = historyList.get(position);
        holder.hisAlbumTitleTV.setText(tracks.albumTitle+"");
        holder.hisArtistNameTV.setText(tracks.trackTitle);
        holder.hisAlbumNbTrackTV.setText(tracks.trackTitle);
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm aa");

        String date = sfd.format(new Date(String.valueOf(historyList.get(position).timestamp.toDate())));
        holder.timeStampTV.setText(date+"");

        String imageUrl = tracks.albumCoverImage;
        Picasso.get().load(imageUrl).into(holder.historyAlbumIV);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView hisArtistNameTV, hisAlbumTitleTV, hisAlbumNbTrackTV, timeStampTV;
        ImageView historyAlbumIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hisArtistNameTV = itemView.findViewById(R.id.hisArtistNameTV);
            hisAlbumTitleTV = itemView.findViewById(R.id.hisAlbumTitleTV);
            hisAlbumNbTrackTV = itemView.findViewById(R.id.hisAlbumNbTrackTV);
            timeStampTV = itemView.findViewById(R.id.timeStampTV);
            historyAlbumIV = itemView.findViewById(R.id.historyAlbumIV);

        }
    }



}
