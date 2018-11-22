package com.blogspot.techtibet.tempapp;

class Comments {
    String name,date,comment;
    public Comments(){

    }

    public Comments(String name, String date, String comment) {
        this.name = name;
        this.date = date;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
