package com.example.finalexam;
/*
Pramukh Nagendra
id: 801167475
FileName: FinalExam_Pramukh
 */
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import static com.example.finalexam.TrackListHorAdapter.mediaPlayer;

public class AlbumActivity extends AppCompatActivity implements AlbumFragment.IAlbumFragmentListener, AlbumSharingFragment.IAlbumSharingListener{

    public static Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        if(getIntent() != null && getIntent().getExtras()!= null && getIntent().hasExtra(MainActivity.ALBUM_KEY)){
            album = (Album) getIntent().getSerializableExtra(MainActivity.ALBUM_KEY);
        }


        getSupportFragmentManager().beginTransaction()
                .add(R.id.albumContentView, new AlbumFragment())
                .addToBackStack(null)
                .commit();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            if (isFinishing()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public Album getAlbumDetails() {
        return album;
    }

    @Override
    public void callAlbumSharingFragment(Album album) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.albumContentView, new AlbumSharingFragment(album))
                .commit();
    }

    @Override
    public void popAlbumSharingFrag() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.albumContentView, new AlbumFragment())
                .commit();
    }
}