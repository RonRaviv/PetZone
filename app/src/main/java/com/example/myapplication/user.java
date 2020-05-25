package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class user {
    private String name;
    private String email;
    private String address;
    //private boolean new_user;
    private static Drawable profile_pic;

    public user(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;


        /*else {
            public_values.db.collection("USER").document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    new_user = documentSnapshot.getBoolean("is_new");
                }
            });
        }*/
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public Drawable getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(Drawable profile_pic) {
        this.profile_pic = profile_pic;
    }
}
