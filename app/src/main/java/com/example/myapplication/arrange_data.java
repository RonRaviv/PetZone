package com.example.myapplication;

import android.graphics.drawable.Drawable;


public class arrange_data {

    private String name;
    private String address;
    private Drawable picture;
    private float rating;
    private int numOfRate = 0;
    private String description;
    private String category;
    private String docRef;
    private String id;
    private String pic_id;

    public arrange_data(String name, String address, Drawable picture, float rating, int num_of_rates, String id, String docRef, String description, String category, String pic_id) {
        this.name = name;
        this.address = address;
        this.picture = picture;
        this.rating = rating;
        this.numOfRate = num_of_rates;
        this.description = description;
        this.category = category;
        this.docRef = docRef;
        this.id = id;
        this.pic_id = pic_id;

        System.out.println("Values: ");
        System.out.println(name);
        System.out.println(address);
        System.out.println(picture);
        System.out.println(rating);
        System.out.println(description);
        System.out.println(docRef);

    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Drawable getPicture() {
        return picture;
    }

    public void setPicture(Drawable picture) {
        this.picture = picture;
    }

    public float getRating() {
        return rating;
    }

    public int getNumOfRate() {
        return numOfRate;
    }

    public void setRating(float rating) {
        this.rating = ((this.rating * numOfRate) + rating) / (numOfRate + 1);
        numOfRate++;
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String getPic_id(){
        return  pic_id;
    }
}
