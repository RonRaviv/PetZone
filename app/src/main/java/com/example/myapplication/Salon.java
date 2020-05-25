package com.example.myapplication;

import android.graphics.Bitmap;

public class Salon {
    private String name;
    private String address;
    private double rating;
    private Bitmap pic;

    public Salon(String name, String address, double rating, Bitmap pic) {
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

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "Salon{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", pic=" + pic +
                '}';
    }
}
