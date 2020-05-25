package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class arrange_comment_data {
    private String name;
    private String date;
    private float rating;
    private String comment_text;
    private Drawable picture;
    private String user_id;

    public arrange_comment_data(){}

    public arrange_comment_data(String name, String date, double rating, String comment_text, Drawable picture, String usesr_Id) {
        this.name = name;
        this.date = date;
        this.rating = (float)rating;
        this.comment_text = comment_text;
        this.picture = picture;
        this.user_id = usesr_Id;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
