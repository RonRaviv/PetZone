package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class Vet {
    private String name;
    private String address;
    private double rating;
    private Drawable pic;

    public Vet(String name, String address, double rating, Drawable pic) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "Vet{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", pic=" + pic +
                '}';
    }
}
