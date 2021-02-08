package com.example.finalexam;

import com.google.firebase.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Tracks implements Serializable {

    long trackId;
    int duration;
    String trackTitle, preview, albumTitle, albumCoverImage;
    Timestamp timestamp;

    public Tracks(JSONObject jsonObject, String albumTitle, String albumCoverImage)throws JSONException {
        this.trackId = jsonObject.getInt("id");
        this.trackTitle = jsonObject.getString("title");
        this.duration = jsonObject.getInt("duration");
        this.preview = jsonObject.getString("preview");
        this.albumTitle = albumTitle;
        this.albumCoverImage = albumCoverImage;
    }

    public Tracks() {
    }

}
