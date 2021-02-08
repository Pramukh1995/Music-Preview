package com.example.finalexam;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TrackListHorAdapter extends RecyclerView.Adapter<TrackListHorAdapter.TracksViewHolder>{

    public static ITrackListListener mListener;
    Context context;
    ArrayList<Tracks> tracksList;
    public static Album albumDetails;
    public static MediaPlayer mediaPlayer;


    public TrackListHorAdapter(ArrayList<Tracks> tracksList, Album albumDetails, Context context, ITrackListListener mListener){
        this.mListener = mListener;
        this.context = context;
        this.tracksList = tracksList;
        this.albumDetails = albumDetails;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public TracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_list_row, parent, false);
        return new TracksViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final TracksViewHolder holder, int position) {
        final Tracks tracks = tracksList.get(position);
        holder.trackTitleTV.setText(tracks.trackTitle+"");
        holder.trackDurationTV.setText(tracks.duration/60+":"+tracks.duration%60);
        holder.layoutPos = position;
        holder.trackInfo = tracks;

    }

    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    public static class TracksViewHolder extends RecyclerView.ViewHolder{

        TextView trackTitleTV, trackDurationTV;
        ImageView playPauseIV;
        int layoutPos;
        Tracks trackInfo;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public TracksViewHolder(@NonNull View itemView) {
            super(itemView);

            trackTitleTV = itemView.findViewById(R.id.trackTitleTV);
            trackDurationTV = itemView.findViewById(R.id.trackDurationTV);
            playPauseIV = itemView.findViewById(R.id.playPauseIV);

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.pause();
                    playPauseIV.setImageResource(R.drawable.play_button);
                }
            });

            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final Date date = new Date();


            //holder.playPauseIV.setTag("pause");
            playPauseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Storing history Data whenever song is played

                    String url = trackInfo.preview;

                    if ( !mediaPlayer.isPlaying()){
                        try {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mListener.updateHistoryList(trackInfo);
                        mediaPlayer.start();
                        playPauseIV.setImageResource(R.drawable.pause_button);

                    }else{
                        mediaPlayer.pause();
                        playPauseIV.setImageResource(R.drawable.play_button);
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.pause();
                            playPauseIV.setImageResource(R.drawable.play_button);
                        }
                    });

                }
            });


        }

    }


    public interface ITrackListListener {
        void updateHistoryList(Tracks tracks);
    }


    /*
    try {
        String url = tracks.preview;//"http://........"; // your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setDataSource(url);
        mediaPlayer.prepare(); // might take long! (for buffering, etc)
        // mediaPlayer.start();
    }catch (IOException e){
        e.printStackTrace();
    }

                if(holder.playPauseIV.getTag().toString().trim().equals("play"))
    {
        holder.playPauseIV.setTag("pause");
        mediaPlayer.pause();

        holder.playPauseIV.setImageResource(R.drawable.play_button);

    }
                else if(holder.playPauseIV.getTag().toString().trim().equals("pause"))
    {
        holder.playPauseIV.setTag("play");
        mediaPlayer.start();
        holder.playPauseIV.setImageResource(R.drawable.pause_button);

    }
    */





}
