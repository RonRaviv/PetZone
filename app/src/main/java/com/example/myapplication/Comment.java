package com.example.myapplication;

public class Comment {
    private String name;
    private String date;
    private float rating;
    private String comment_text;
    private String user_id;

    public Comment() {
    }

    public Comment(String name, String date, float rating, String comment_text, String user_id) {
        this.name = name;
        this.date = date;
        this.rating = rating;
        this.comment_text = comment_text;
        this.user_id = user_id;
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
}
