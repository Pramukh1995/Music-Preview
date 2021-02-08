package com.example.finalexam;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Album implements Serializable {

    long id, nb_tracks;
    String albumTitle, artistName, albumCoverImage, artistCoverImage, trackList, sharedUserName;
    Timestamp timestamp;

    public Album(JSONObject jsonObject)throws JSONException{
        this.id = jsonObject.getLong("id");
        this.albumTitle = jsonObject.getString("title");
        this.artistName = jsonObject.getJSONObject("artist").getString("name");
        this.nb_tracks = jsonObject.getInt("nb_tracks");
        this.albumCoverImage = jsonObject.getString("cover_medium");
        this.artistCoverImage = jsonObject.getJSONObject("artist").getString("picture_small");
        this.trackList = jsonObject.getString("tracklist");
    }

    public Album() {
    }


    @Override
    public String toString() {
        return "album{" +
                "id=" + id +
                ", albumTitles='" + albumTitle + '\'' +
                ", artisName='" + artistName + '\'' +
                ", noOfTracks='" + nb_tracks + '\'' +
                ", alblumCoverImage='" + albumCoverImage + '\'' +
                '}';
    }
}
