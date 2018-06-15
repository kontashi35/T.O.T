package com.blogspot.techtibet.tempapp;

class Videos extends  VideosId{
    String video_name,time,user,video_url,thumb_url;
    long view_count;

    public Videos(long view_count) {
        this.view_count = view_count;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public Videos(){}
    public Videos(String video_name, String time, String user, String video_url) {
        this.video_name = video_name;
        this.time = time;
        this.user = user;
        this.video_url = video_url;
        this.thumb_url = thumb_url;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public long getView_count() {
        return view_count;
    }

    public void setView_count(long view_count) {
        this.view_count = view_count;
    }
}
