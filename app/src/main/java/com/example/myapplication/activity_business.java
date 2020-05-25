package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class activity_business extends AppCompatActivity implements View.OnClickListener {

    boolean return_value_new = false;

    DocumentReference page_doc;
    Intent intent;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu_button;
    EditText name;
    EditText description;
    EditText address;
    ImageView edit_button;
    ImageView confirm_changes;
    ImageView cancel_changes;
    String temp_name;
    String temp_des;
    ImageView picture;
    RatingBar ratingBar;
    ArrayList<arrange_comment_data> arrange_comment_data_list = new ArrayList<>();
    ListView listView;
    comment_adapter adapter;
    ImageView profile_pic_tb;

    Uri pic;
    final static int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        profile_pic_tb = findViewById(R.id.profile_pic_tb);
        intent = getIntent();
        listView = findViewById(R.id.comments_list_view_bPage);
        ratingBar = findViewById(R.id.place_ratingBar_bPage);
        picture = findViewById(R.id.place_pic_bPage);
        picture.setOnClickListener(this);
        picture.setClickable(false);
        name = findViewById(R.id.place_name_bPage);
        description = findViewById(R.id.place_description_bPage);
        address = findViewById(R.id.place_address_bPage);
        edit_button = findViewById(R.id.edit_button_bPage);
        edit_button.setOnClickListener(this);
        confirm_changes = findViewById(R.id.save_button_bPage);
        confirm_changes.setOnClickListener(this);
        cancel_changes = findViewById(R.id.cancel_button_bPage);
        cancel_changes.setOnClickListener(this);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        drawerLayout = findViewById(R.id.bPage_drawer_layout);
        navigationView = findViewById(R.id.bPage_nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.business_page).setVisible(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            /*print message on screen when something on menu pressed (temp option)*/
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        if(public_values.mAuth.getCurrentUser() != null){
                            intent = new Intent(activity_business.this, activity_profile.class);
                        }
                        else {
                            intent = new Intent(activity_business.this, activity_login.class);
                        }
                        startActivity(intent);
                        break;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_business.this, activity_saved.class);
                        startActivity(intent);
                        break;

                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        break;
                }
                return true;
            }
        });


        name.setTag(name.getKeyListener());
        name.setKeyListener(null);
        description.setTag(description.getKeyListener());
        description.setKeyListener(null);
        address.setTag(address.getKeyListener());
        address.setKeyListener(null);

        //comments
        adapter = new comment_adapter(this, 0, 0, arrange_comment_data_list, activity_business.this);
        listView = findViewById(R.id.comments_list_view_bPage);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<business_user>(){}.getType();
        String json = sharedPreferences.getString("user",null);
        public_values.current_bUser = gson.fromJson(json, type);*/
        if (check_new()) {
            get_data();
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.edit_button_bPage:
                /*name.setInputType(InputType.TYPE_CLASS_TEXT);
                description.setInputType(InputType.TYPE_CLASS_TEXT);*/
                picture.setClickable(true);
                edit_button.setVisibility(View.GONE);
                confirm_changes.setVisibility(View.VISIBLE);
                cancel_changes.setVisibility(View.VISIBLE);
                name.setKeyListener((KeyListener) name.getTag());
                description.setKeyListener((KeyListener) description.getTag());
                address.setKeyListener((KeyListener) address.getTag());
                temp_name = name.getText().toString();
                temp_des = description.getText().toString();
                break;

            case R.id.save_button_bPage:
                String cat;
                if(check_new()) {
                    intent = getIntent();
                    cat = intent.getStringExtra("business_cat").toUpperCase();
                }
                else {
                    //business_user temp_user = (business_user) public_values.current_user;
                    cat = public_values.current_bUser.getCategory();
                }
                if(name.getText() == null || name.getText().toString().trim().equals("")){
                    name.setText("ללא שם");
                }
                if(description.getText() == null || description.getText().toString().trim().equals("")){
                    description.setText("ללא תיאור");
                }
                if(address.getText() == null || address.getText().toString().trim().equals("")){
                    address.setText("ללא כתובת");
                }
                final Map<String, Object> new_page = new HashMap<>();
                new_page.put("name", name.getText().toString());
                new_page.put("description", description.getText().toString());
                if(check_new()) {
                    new_page.put("rating", 0.1);
                    new_page.put("email", public_values.mAuth.getCurrentUser().getEmail());
                }
                new_page.put("address", address.getText().toString());
                new_page.put("pic_id", public_values.UID);

                if(check_new()) {
                    DocumentReference doc = public_values.db.collection(cat).document();
                    page_doc = doc;
                    page_doc.set(new_page);
                    public_values.db.collection("BUSINESS_USERS").document(public_values.UID).update("page", doc);
                    //page_doc.update("page", doc);
                    //set_new_user();
                }
                else {
                    DocumentReference docRef = public_values.db.collection("BUSINESS_USERS").document(public_values.UID);
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            page_doc = documentSnapshot.getDocumentReference("page");
                            page_doc.update(new_page);
                        }
                    });
                }
                end_edit();
                break;

            case R.id.cancel_button_bPage:
                name.setTag(name.getKeyListener());
                name.setKeyListener(null);
                description.setTag(description.getKeyListener());
                description.setKeyListener(null);

                name.setText(temp_name);
                description.setText(temp_des);
                //TODO restore pictures
                end_edit();
                break;

            case R.id.place_pic_bPage:
                openFileChooser();
                break;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); //get only images
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            pic = data.getData();
            Glide.with(this).load(pic).into(picture);
        }
        uploadImage();
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("מעלה...");
        progressDialog.show();

        //save to storage
        StorageReference storage = public_values.mStorageRef.getReference().child("businessPics/" + public_values.UID);
        storage.putFile(pic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressDialog.dismiss();
                    }
                });
    }

    public boolean check_new(){
        /*if(public_values.current_bUser.isNew_user()){
            public_values.current_bUser.setNew_user(false);
            return true;
        }
        return false;*/





        try {
            public_values.db.collection("BUSINESS_USERS").document(public_values.UID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if(doc.getBoolean("is_new")) {
                            return_value_new = true;
                            public_values.db.collection("BUSINESS_USERS").document(public_values.UID).update("is_new", false);
                        }
                        else {
                            return_value_new = false;
                            page_doc = doc.getDocumentReference("page");
                        }
                    }
                });
        }
        catch (NullPointerException e){
            Log.d(public_values.TAG, "document path does not exist ", e);
        }


        /*if(public_values.current_bUser == null){
            public_values.db.collection("BUSINESS_USERS").document(public_values.mAuth.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot doc) {
                    public_values.current_bUser = new business_user(doc.getString("name"), doc.getString("email"), doc.getString("address"),doc.getDocumentReference("business_page"), doc.getString("category"));
                    return_value_new = public_values.current_bUser.isNew_user();
                    public_values.current_bUser.setNew_user(false);
                    public_values.db.collection("BUSINESS_USERS").document(public_values.UID).update("is_new", false);
                }
            });
        }
        else {
            return_value_new = public_values.current_bUser.isNew_user();
            public_values.current_bUser.setNew_user(false);
            public_values.db.collection("BUSINESS_USERS").document(public_values.UID).update("is_new", false);
        }*/
        return return_value_new;

        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type = new TypeToken<business_user>(){}.getType();
        business_user user = gson.fromJson(json, type);
        if(user.isNew_user()){
            return true;
        }
        page_doc = user.getPage();*/
        //return false;
    }

    public void set_new_user(){
        //set user to not be new user
        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type = new TypeToken<business_user>(){}.getType();
        business_user user = gson.fromJson(json, type);
        user.setNew_user(false);
        public_values.current_bUser = user;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();*/




    }

    public void end_edit(){
        picture.setClickable(false);
        name.setTag(name.getKeyListener());
        name.setKeyListener(null);
        description.setTag(description.getKeyListener());
        description.setKeyListener(null);
        address.setTag(address.getKeyListener());
        address.setKeyListener(null);

        edit_button.setVisibility(View.VISIBLE);
        confirm_changes.setVisibility(View.GONE);
        cancel_changes.setVisibility(View.GONE);
    }

    public void get_data(){
        //business_user temp_user = (business_user) public_values.current_user;
        DocumentReference page_data = public_values.current_bUser.getPage();

        //final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pet_zone_logo);//TODO replace bitmap with pic from storage and save as final

        /*get comments data*/
        try {
            page_data.collection("COMMENTS").orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(public_values.TAG, document.getId() + " => " + document.getData());

                                    final arrange_comment_data comment = new arrange_comment_data(document.get("name").toString(),
                                            document.get("date").toString(), (double) document.get("rating"),
                                            document.get("comment_text").toString(), null, document.getString("user_id"));

                                    StorageReference storageReference = public_values.mStorageRef.getReference().child("businessPics/" + public_values.UID);
                                    ImageView temp_image = new ImageView(getBaseContext());
                                    temp_image.setVisibility(View.GONE);
                                    Glide.with(activity_business.this).load(storageReference).fallback(R.drawable.ic_no_profile_photo)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    comment.setPicture(resource);
                                                    arrange_comment_data_list.add(comment);
                                                    adapter.notifyDataSetChanged();
                                                    return false;
                                                }
                                            }).into(temp_image);
                                }

                            } else {
                                Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        catch (NullPointerException e){
            Log.d(public_values.TAG, "no comments", e);
        }


        page_data.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("name"));
                address.setText(documentSnapshot.getString("address"));
                description.setText(documentSnapshot.getString("description"));
                ratingBar.setRating(documentSnapshot.getDouble("rating").floatValue());

                StorageReference storageReference = public_values.mStorageRef.getReference().child("businessPics" + public_values.UID);
                Glide.with(activity_business.this).load(storageReference).into(picture);
                set_profile_pic_tb();
            }
        });

    }

    public void set_profile_pic_tb(){
        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(activity_business.this).load(download_url).into(profile_pic_tb);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}