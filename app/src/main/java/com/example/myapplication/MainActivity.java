package com.example.myapplication;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{




    DrawerLayout drawerLayout;
    NavigationView navigationView;

    ImageView vet;
    ImageView walker;
    ImageView salon;
    ImageView pension;
    Intent intent;
    ImageView menu_button;
    MenuItem profile;
    Toolbar toolbar;
    ImageView profile_pic_tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*buttons*/
        vet = findViewById(R.id.vet);
        vet.setOnClickListener(this);
        salon = findViewById(R.id.salon);
        salon.setOnClickListener(this);
        walker = findViewById(R.id.dog_walker);
        walker.setOnClickListener(this);
        pension = findViewById(R.id.pension);
        pension.setOnClickListener(this);
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        profile = findViewById(R.id.profile);
        toolbar = findViewById(R.id.app_toolbar);
        profile_pic_tb = findViewById(R.id.profile_pic_tb);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.Nav_view);
        if(public_values.mAuth.getCurrentUser() != null) {
            if(public_values.is_business_user) {
                Menu menu =  navigationView.getMenu();
                menu.findItem(R.id.business_page).setVisible(true);
            }
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            /*print message on screen when something on menu pressed (temp option)*/
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                System.out.println(public_values.is_business_user);
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        if(public_values.mAuth.getCurrentUser() != null){
                            intent = new Intent(MainActivity.this, activity_profile.class);
                        }
                        else {
                            intent = new Intent(MainActivity.this, activity_login.class);
                        }
                        startActivity(intent);
                        break;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(MainActivity.this, activity_saved.class);
                        startActivity(intent);
                        break;
                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(MainActivity.this, activity_business.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        //get basic info about the user (email & name)
        if(public_values.mAuth.getCurrentUser() != null){
            get_user_type();
        }
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_background_main));

        //empty saved list
        /*SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SAVED, MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<arrange_data> temp_saved_list = new ArrayList<>();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(temp_saved_list);
        editor.putString(SAVED_LIST, json);
        editor.apply();*/

        /*SharedPreferences sharedPreferences = getSharedPreferences(public_values.SHARED_PREFS_SAVED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();

        SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.clear().apply();*/
        //log out
        //public_values.mAuth.signOut();
    }

    public void get_user_type(){
        public_values.db.collection("BUSINESS_USERS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //public_values.user_email = public_values.mAuth.getCurrentUser().getEmail();
                public_values.UID = public_values.mAuth.getUid();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.getId().equals(public_values.UID)){
                        public_values.is_business_user = true;
                        break;
                    }
                }
                String coll;
                if(public_values.is_business_user){
                    coll = "BUSINESS_USERS";
                    Menu menu =  navigationView.getMenu();
                    menu.findItem(R.id.business_page).setVisible(true);
                }
                else {
                    coll = "USERS";
                }
                //public_values.user_email = public_values.mAuth.getCurrentUser().getEmail();
                public_values.UID = public_values.mAuth.getUid();
                DocumentReference docRef = public_values.db.collection(coll).document(public_values.mAuth.getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        System.out.println("############################################");
                        public_values.user_name = (String) documentSnapshot.get("name");
                        /*SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_name", user_name);
                        editor.apply();*/


                        get_user_data();

                        //profile_pic_tb.setImageDrawable(public_values.current_user.getProfile_pic());

                    }
                });
            }
        });
    }

    public void get_user_data(){
        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type;
        System.out.println(sharedPreferences.getString("user", null));*/
        final ImageView temp = new ImageView(getBaseContext());







        if(public_values.is_business_user) {
            /*type = new TypeToken<business_user>(){}.getType();
            public_values.current_bUser = gson.fromJson(json, type);*/

            if (public_values.current_bUser == null){
                public_values.db.collection("BUSINESS_USERS").document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        public_values.current_bUser = new business_user(doc.getString("name"), doc.getString("email"), doc.getString("address"), doc.getDocumentReference("page"), doc.getString("category"));


                        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_url = uri.toString();
                                Glide.with(MainActivity.this).load(download_url).into(profile_pic_tb);
                            }
                        });

                    }
                });
            }








        }
        else{
            /*type = new TypeToken<user>(){}.getType();
            public_values.current_user = gson.fromJson(json, type);*/




            if(public_values.current_user == null) {
                public_values.db.collection("BUSINESS_USERS").document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        public_values.current_user = new user(doc.getString("name"), doc.getString("email"), doc.getString("address"));


                        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.UID);
                        Glide.with(MainActivity.this).load(ref).fallback(R.drawable.ic_no_profile_photo).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                public_values.current_user.setProfile_pic(temp.getDrawable());
                                Glide.with(MainActivity.this).load(public_values.current_user.getProfile_pic()).into(profile_pic_tb);
                                return false;
                            }
                        }).into(temp);
                    }
                });
            }




        }
    }

    /*react to button press*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.vet:
                intent = new Intent(this, activity_vet.class);
                intent.putExtra("category", "vet");
                startActivity(intent);
                break;

            case R.id.pension:
                intent = new Intent(this, activity_pension.class);
                intent.putExtra("category", "pension");
                startActivity(intent);
                break;

            case R.id.salon:
                intent = new Intent(this, activity_salon.class);
                intent.putExtra("category", "salon");
                startActivity(intent);
                break;

            case R.id.dog_walker:
                intent = new Intent(this, activity_walker.class);
                intent.putExtra("category", "walker");
                startActivity(intent);
                break;
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

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
