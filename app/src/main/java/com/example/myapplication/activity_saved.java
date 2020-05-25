package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class activity_saved extends AppCompatActivity implements View.OnClickListener {

    ImageView profile_pic_tb;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    adapter adapter;
    ListView listView;
    ImageView menu_button;
    ArrayList<arrange_data> arrayList;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        /*menu + drawer*/
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        drawerLayout = findViewById(R.id.category_drawer_layout);
        navigationView = findViewById(R.id.category_nav_view);

        profile_pic_tb = findViewById(R.id.profile_pic_tb);
        intent = getIntent();
        if(public_values.mAuth.getCurrentUser() != null) {
            load_data();
            set_profile_pic_tb();
            /*scroll view*/
            adapter = new adapter(this, 0, 0, arrayList, activity_saved.this);
            listView = findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setDivider(null);
            listView.setDividerHeight(0);
            adapter.notifyDataSetChanged();

            if(public_values.is_business_user) {
                Menu menu =  navigationView.getMenu();
                menu.findItem(R.id.business_page).setVisible(true);
            }
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        if(public_values.mAuth.getCurrentUser() != null){
                            intent = new Intent(activity_saved.this, activity_profile.class);
                        }
                        else {
                            intent = new Intent(activity_saved.this, activity_login.class);
                        }
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        break;

                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_saved.this, activity_business.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    public void set_profile_pic_tb(){
        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(activity_saved.this).load(download_url).into(profile_pic_tb);
            }
        });
    }

    public void load_data(){
        SharedPreferences sharedPreferences = getSharedPreferences(public_values.SHARED_PREFS_SAVED, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(public_values.SAVED_LIST, null);
        Type type = new TypeToken<ArrayList<arrange_data>>() {}.getType();
        arrayList = gson.fromJson(json, type);
        if(arrayList == null) {
            arrayList = new ArrayList<>();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu_button) {
            Toast.makeText(activity_saved.this, "menu", Toast.LENGTH_SHORT).show();
            drawerLayout.openDrawer(GravityCompat.END);
        }

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
