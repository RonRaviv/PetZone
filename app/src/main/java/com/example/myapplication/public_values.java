package com.example.myapplication;

import android.graphics.drawable.Drawable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class public_values {

    //log
    public static final String TAG = "tag";

    //Initialize Firebase
    final static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final static FirebaseStorage mStorageRef = FirebaseStorage.getInstance();

    //SharedPreferences
    final static String SHARED_PREFS_SAVED = "saved";
    final static String SAVED_LIST = "saved_list";

    //user data & values
    public static String user_email = null;
    static boolean is_business_user = false;
    static String user_name = "אורח";
    public static user current_user;
    public static business_user current_bUser;
    public static Drawable user_pic;
    public static String UID;
}
