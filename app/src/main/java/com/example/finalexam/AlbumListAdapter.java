package com.example.finalexam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.albumViewHolder> {
    Context context;
    IAlbumListAdapter mListener;
    ArrayList<Album> albumsList;
    ArrayList<Album> albumsLiked;

    public AlbumListAdapter(ArrayList<Album> albums, ArrayList<Album> albumsLiked,Context context, IAlbumListAdapter mListener){
        this.mListener = mListener;
        this.context = context;
        this.albumsList = albums;
        this.albumsLiked = albumsLiked;
    }

    @NonNull
    @Override
    public albumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_list_row, parent, false);
        return new albumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull albumViewHolder holder, int position) {
        final Album album = albumsList.get(position);
        holder.albumTitleTV.setText(album.albumTitle+"");
        holder.artistNameTV.setText(album.artistName+"");
        holder.albumNoOfTracksTV.setText(album.nb_tracks+"");
        String imageUrl = album.albumCoverImage;
        Picasso.get().load(imageUrl).into(holder.albumImageView);

        holder.albumLikeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.likesDislikeFunctionality(album);
            }
        });

        boolean bln = false;
        for(Album albumTemp: albumsLiked){
            if(albumTemp.id == album.id){
                holder.albumLikeIV.setImageResource(R.drawable.like_favorite);
                bln = true;
                break;
            }
        }
        if(!bln) holder.albumLikeIV.setImageResource(R.drawable.like_not_favorite);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.callAlbumActivity(album);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public static class albumViewHolder extends RecyclerView.ViewHolder{
        ImageView albumImageView, albumLikeIV;
        TextView albumTitleTV, artistNameTV, albumNoOfTracksTV;

        public albumViewHolder(@NonNull View itemView) {
            super(itemView);

            albumImageView = itemView.findViewById(R.id.albumImageView);
            albumTitleTV = itemView.findViewById(R.id.albumTitleTV);
            artistNameTV = itemView.findViewById(R.id.artistNameTV);
            albumNoOfTracksTV = itemView.findViewById(R.id.albumNoOfTracksTV);
            albumLikeIV = itemView.findViewById(R.id.albumLikeIV);

        }
    }

    public interface IAlbumListAdapter {
        void callAlbumActivity(Album album);
        void likesDislikeFunctionality(Album album);
    }
}
