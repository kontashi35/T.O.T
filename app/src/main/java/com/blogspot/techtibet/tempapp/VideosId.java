package com.blogspot.techtibet.tempapp;

import android.support.annotation.NonNull;

public class VideosId {
    public String videosId;
    public <T extends VideosId> T withId(@NonNull final String id){
        this.videosId=id;
        return  (T) this;
    }
}
