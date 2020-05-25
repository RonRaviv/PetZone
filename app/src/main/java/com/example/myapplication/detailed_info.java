package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class detailed_info extends AppCompatActivity implements View.OnClickListener {

    DocumentReference currentDoc;
    arrange_data page_data = null;
    ArrayList<arrange_data> temp_saved_list;
    ProgressDialog progressDialog;

    ImageView profile_pic_tb;
    TextView place_address;
    RatingBar place_rating;
    TextView place_name;
    TextView place_description;
    ImageView place_pic;
    CheckBox save_button;
    TextView comment_title;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Intent intent;
    ImageView menu_button;
    RatingBar rate_place;
    RatingBar popup_rating;
    ImageView cancel_comment;
    TextView popup_title;
    ArrayList<arrange_comment_data> arrange_comment_data_list = new ArrayList<>();
    comment_adapter adapter;
    ListView listView;
    String category;
    String id;
    boolean ret_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info);
        progressDialog = new ProgressDialog(this);

        profile_pic_tb = findViewById(R.id.profile_pic_tb);
        comment_title = findViewById(R.id.comment_rate_title);
        place_address = findViewById(R.id.place_address_detailed);
        place_name = findViewById(R.id.place_name_detailed);
        place_description = findViewById(R.id.place_description);
        place_pic = findViewById(R.id.place_pic_detailed);
        place_pic.setOnClickListener(this);
        save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(this);
        drawerLayout = findViewById(R.id.category_drawer_layout);
        navigationView = findViewById(R.id.category_nav_view);
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        rate_place = findViewById(R.id.rate_place);
        rate_place.setOnClickListener(this);
        place_rating = findViewById(R.id.place_ratingBar);


        intent = getIntent();
        category = intent.getStringExtra("category");
        id = intent.getStringExtra("id");
        Get_Doc();
        //currentDoc = db.collection(category.toUpperCase().substring(9) + "S").document(id);

        if(public_values.mAuth.getCurrentUser() != null) {
            if(public_values.is_business_user) {
                Menu menu =  navigationView.getMenu();
                menu.findItem(R.id.business_page).setVisible(true);
            }
        }
        // drawer menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        if(public_values.mAuth.getCurrentUser() != null){
                            intent = new Intent(detailed_info.this, activity_profile.class);
                        }
                        else {
                            intent = new Intent(detailed_info.this, activity_login.class);
                        }
                        startActivity(intent);
                        break;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(detailed_info.this, activity_saved.class);
                        startActivity(intent);
                        break;

                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(detailed_info.this, activity_business.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //rate & comment
        rate_place.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {
                if(fromUser) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(detailed_info.this);
                    View view = getLayoutInflater().inflate(R.layout.comment_popup_window, null);
                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final EditText comment_text = view.findViewById(R.id.popup_comment_text);

                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "הגב", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);

                            Date time = Calendar.getInstance().getTime();
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
                            String formattedDate = format.format(time);

                            if(comment_text.getText() == null || comment_text.getText().toString().trim().equals(""))
                                comment_text.setText("ללא תגובה");

                            Comment comment = new Comment(sharedPreferences.getString("user_name", ""),
                                    formattedDate, popup_rating.getRating(), comment_text.getText().toString(), public_values.UID);
                            String coll = category.toUpperCase().substring(9) + "S";
                            public_values.db.collection(coll).document(id).collection("COMMENTS").add(comment);
                            arrange_comment_data data = new arrange_comment_data(sharedPreferences.getString("user_name", ""),
                                    formattedDate, popup_rating.getRating(), comment_text.getText().toString(), null, public_values.UID);
                            arrange_comment_data_list.add(data);
                            adapter.notifyDataSetChanged();

                            page_data.setRating(rating);
                            currentDoc.update("rating", page_data.getRating());
                        }
                    });
                    dialog.show();
                    popup_rating = view.findViewById(R.id.popup_rating);
                    popup_rating.setRating(rating);
                    popup_title = view.findViewById(R.id.comment_title);
                    //close window if X pressed
                    cancel_comment = view.findViewById(R.id.cancel_comment);
                    cancel_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    ratingBar.setRating(0);
                }
            }
        });


        //comments
        adapter = new comment_adapter(this, 0, 0, arrange_comment_data_list, detailed_info.this);
        listView = findViewById(R.id.comments_list_view);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);


        place_rating.setVisibility(View.INVISIBLE);
        place_name.setVisibility(View.INVISIBLE);
        place_description.setVisibility(View.INVISIBLE);
        place_pic.setVisibility(View.INVISIBLE);
        save_button.setVisibility(View.INVISIBLE);
        rate_place.setVisibility(View.INVISIBLE);
        place_address.setVisibility(View.INVISIBLE);
        comment_title.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        progressDialog.setTitle("טוען...");
        progressDialog.show();
        get_data();
        //TODO add GOOD delay (1000ms)



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.save_button:
                //if logged in
                if(public_values.mAuth.getCurrentUser() != null){
                    String coll;
                    if(public_values.is_business_user){
                        coll = "BUSINESS_USERS";
                    }
                    else{
                        coll = "USERS";
                    }
                    CollectionReference userSavedColl = public_values.db.collection(coll).document(public_values.UID).collection("SAVED");
                    if(save_button.isChecked()) {
                        //add to saved in database

                        Toast.makeText(detailed_info.this, "נשמר למועדפים", Toast.LENGTH_SHORT).show();
                        Map<String, Object> save = new HashMap<>();
                        save.put("reference", currentDoc);
                        /*save.put("name", place_name.getText());
                        save.put("address", place_address);
                        save.put("rating", place_rating.getRating());*/

                        /*add to saved in database*/
                        userSavedColl.add(save);
                        /*add to saved in device storage*/
                        manage_saved_storage("add");
                        break;
                    }
                    Toast.makeText(detailed_info.this, "נמחק ממועדיפים", Toast.LENGTH_SHORT).show();

                    /*remove from saved in database*/
                    userSavedColl.document(currentDoc.getPath()).delete();
                    /*remove from saved in device storage*/
                    manage_saved_storage("remove");
                    break;
                }
                save_button.setClickable(false);
                save_button.setChecked(false);
                Toast.makeText(detailed_info.this, "תתחבר כדי לשמור", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*public void is_saved(){
        String coll;
        if(public_values.is_business_user){
            coll = "BUSINESS_USERS";
        }
        else{
            coll = "USERS";
        }
        CollectionReference collRef = public_values.db.collection(coll).document(public_values.UID).collection("SAVED");
        collRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        if(doc.getDocumentReference("reference").getPath().equals(page_data.getDocRef())){
                            save_button.setChecked(true);
                        }
                    }
                }
            }
        });
    }*/

    public void Get_Doc(){
        if(intent.getStringExtra("category").equals("activity_saved")){
            currentDoc = public_values.db.document(intent.getStringExtra("ref"));
            System.out.println(currentDoc);
            /*System.out.println("ppppppppppppppppppppppppppppppppppppppppppppppp");

            System.out.println(db.collection("USERS"));
            System.out.println(db.collection("USERS").document(public_values.user_email));
            System.out.println(db.collection("USERS").document(public_values.user_email).collection("SAVED"));

            CollectionReference user_saved = db.collection("USERS").document(public_values.user_email).collection("SAVED");
            user_saved.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        System.out.println("lllllllllllllllllllllllllllllllllllll");
                        //System.out.println(doc);
                        //System.out.println(doc.get("reference"));

                        System.out.println(intent.getStringExtra("ref"));
                        if(doc.getDocumentReference("reference").getPath().equals(intent.getStringExtra("ref"))){
                            System.out.println("yayyyyyyyyyyyyyyyyyy");
                           currentDoc = doc.getReference();
                           break;
                       }
                    }
                }
            });*/
        }
        else {
            currentDoc = public_values.db.collection(category.toUpperCase().substring(9) + "S").document(id);
        }
    }

    public void manage_saved_storage(String function){
        /*add/remove from saved in device storage*/

            //read existing list
        SharedPreferences sharedPreferences = getSharedPreferences(public_values.SHARED_PREFS_SAVED, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(public_values.SAVED_LIST, null);
        Type type = new TypeToken<ArrayList<arrange_data>>(){}.getType();
        temp_saved_list = gson.fromJson(json, type);
        if(temp_saved_list == null){
            temp_saved_list = new ArrayList<>();
        }

        // add/remove from list and re-save it
        if(function.equals("add")) {
            temp_saved_list.add(page_data);
        }else {
            temp_saved_list.remove(page_data);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        json = gson.toJson(temp_saved_list);
        editor.putString(public_values.SAVED_LIST, json);
        editor.apply();
    }

    public void get_data (){
        /*get comments data*/
        currentDoc.collection("COMMENTS").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(public_values.TAG, document.getId() + " => " + document.getData());

                                final arrange_comment_data comment = new arrange_comment_data(document.get("name").toString(),
                                        document.get("date").toString(), (double)document.get("rating"),
                                        document.get("comment_text").toString(), null, document.getString("user_id"));

                                arrange_comment_data_list.add(comment);
                                adapter.notifyDataSetChanged();
                                /*StorageReference storageReference = public_values.mStorageRef.getReference().child("businessPics/" + public_values.UID);
                                ImageView temp_image = new ImageView(getBaseContext());
                                temp_image.setVisibility(View.GONE);
                                Glide.with(detailed_info.this).load(storageReference).fallback(R.drawable.ic_no_profile_photo)
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
                                }).into(temp_image);*/



                            }

                            progressDialog.dismiss();
                            place_rating.setVisibility(View.VISIBLE);
                            place_name.setVisibility(View.VISIBLE);
                            place_description.setVisibility(View.VISIBLE);
                            place_pic.setVisibility(View.VISIBLE);
                            save_button.setVisibility(View.VISIBLE);
                            rate_place.setVisibility(View.VISIBLE);
                            place_address.setVisibility(View.VISIBLE);
                            comment_title.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*get page data*/
        currentDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    page_data = new arrange_data(doc.getString("name"), doc.getString("address"), null, doc.getDouble("rating").floatValue(), doc.getDouble("num_of_rates").intValue(), doc.getId(), doc.getReference().getPath(), doc.getString("description"), null, doc.getString("pic_id"));
                    place_name.setText(page_data.getName());
                    place_description.setText(page_data.getDescription());
                    place_rating.setRating(page_data.getRating());
                    place_address.setText(page_data.getAddress());
                    StorageReference storageReference = public_values.mStorageRef.getReference().child("businessPics/" + page_data.getPic_id());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                            String download_url = uri.toString();
                            Glide.with(detailed_info.this).load(download_url).fallback(R.drawable.ic_no_profile_photo).into(place_pic);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("/////////////////////////////////////////////");
                        }
                    });
                    //public_values.mStorageRef.

                }
                /*if(public_values.mAuth.getCurrentUser() != null) {
                    is_saved();
                    set_profile_pic_tb();
                }*/
                /*if(public_values.mAuth.getCurrentUser() != null){
                    is_saved();
                    if(ret_value){
                        save_button.setChecked(true);
                    }
                }*/
            }
        });


    }

    public void set_profile_pic_tb(){
        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(detailed_info.this).load(download_url).into(profile_pic_tb);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
