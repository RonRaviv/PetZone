package com.example.myapplication;

import com.google.firebase.firestore.DocumentReference;

public class business_user extends user{
    private DocumentReference page;
    private String category;
    //private boolean new_user = true;

    public business_user(String name, String email, String address, DocumentReference page, String category) {
        super(name, email, address);
        this.page = page;
        this.category = category;

        //check (and set) if new user
        /*try {
            public_values.db.collection("BUSINESS_USERS").document(public_values.UID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    new_user = documentSnapshot.getBoolean("is_new");
                }
            });
        }
        catch (NullPointerException e){
            Log.d(public_values.TAG,"document path does not exist ", e);
        }*/
    }

    public DocumentReference getPage() {
        return page;
    }

    public void setPage(DocumentReference page) {
        this.page = page;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /*public boolean isNew_user() {
        return new_user;
    }

    public void setNew_user(boolean new_user) {
        this.new_user = new_user;
    }*/
}




/*
public class business_user {
    private String name;
    private String email;
    private DocumentReference page;
    private boolean new_user = true;

    public business_user(String name, String email, DocumentReference page) {
        this.name = name;
        this.email = email;
        this.page = page;
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

    public DocumentReference getPage() {
        return page;
    }

    public void setPage(DocumentReference page) {
        this.page = page;
    }

    public boolean isNew_user() {
        return new_user;
    }

    public void setNew_user(boolean new_user) {
        this.new_user = new_user;
    }
}
*/
