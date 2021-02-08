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

public class SharedListAdapter extends RecyclerView.Adapter<SharedListAdapter.ViewHolder>{

    ArrayList<Album> sharedAlbumsList;
    SharedAdapterInterface sharedAdapterInterface;

    public SharedListAdapter(ArrayList<Album> allAlbumsShared,  SharedAdapterInterface sharedFragmentAdapterInterface){
        this.sharedAlbumsList = allAlbumsShared;
        this.sharedAdapterInterface = sharedFragmentAdapterInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Album album = sharedAlbumsList.get(position);
        holder.sharedAlbumTitleTV.setText(album.albumTitle);
        holder.sharedArtistNameTV.setText(album.artistName);
        holder.sharedNbTrackTV.setText(album.nb_tracks+"");
        holder.sharedUserNameTV.setText(album.sharedUserName);

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm aa");
        String date = sfd.format(new Date(String.valueOf(sharedAlbumsList.get(position).timestamp.toDate())));
        holder.sharedTimeStampTV.setText(date);

        Picasso.get().load(album.albumCoverImage).into(holder.sharedAlbumIV);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedAdapterInterface.callAlbumActivity(album);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sharedAlbumsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView sharedAlbumTitleTV, sharedArtistNameTV, sharedNbTrackTV, sharedUserNameTV, sharedTimeStampTV;
        ImageView sharedAlbumIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sharedAlbumTitleTV = itemView.findViewById(R.id.sharedAlbumTitleTV);
            sharedArtistNameTV = itemView.findViewById(R.id.sharedArtistNameTV);
            sharedNbTrackTV = itemView.findViewById(R.id.sharedNbTrackTV);
            sharedUserNameTV = itemView.findViewById(R.id.sharedUserNameTV);
            sharedTimeStampTV = itemView.findViewById(R.id.sharedTimeStampTV);
            sharedAlbumIV = itemView.findViewById(R.id.sharedAlbumIV);

        }
    }

    public interface SharedAdapterInterface {
        void callAlbumActivity(Album album);
    }

}
