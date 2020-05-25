package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public abstract class activity_category extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    adapter adapter;
    ListView listView;
    ImageView menu_button;
    EditText search_bar;
    Intent intent;
    ArrayList<arrange_data> arrayList;
    ArrayList<Vet> vets_list = new ArrayList<>();
    ArrayList<Pension> pensions_list = new ArrayList<>();
    ArrayList<Salon> salons_list = new ArrayList<>();
    ArrayList<Walker> walkers_list = new ArrayList<>();
    ImageView profile_pic_tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        search_bar = findViewById(R.id.search_bar);
        profile_pic_tb = findViewById(R.id.profile_pic_tb);
        intent = getIntent();

        /*menu + drawer*/
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        drawerLayout = findViewById(R.id.category_drawer_layout);
        navigationView = findViewById(R.id.category_nav_view);

        /*scroll view*/
        arrayList = new ArrayList<>();
        adapter = new adapter(this, 0, 0, arrayList, activity_category.this);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setDividerHeight(0);


        generateDataBlocks("rating");
        if(public_values.mAuth.getCurrentUser() != null) {
            set_profile_pic_tb();
            if(public_values.is_business_user) {
                Menu menu =  navigationView.getMenu();
                menu.findItem(R.id.business_page).setVisible(true);
            }
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /*print message on screen when something on menu pressed (temp option)*/
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        if(public_values.mAuth.getCurrentUser() != null){
                            intent = new Intent(activity_category.this, activity_profile.class);
                        }
                        else {
                            intent = new Intent(activity_category.this, activity_login.class);
                        }
                        startActivity(intent);
                        break;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_category.this, activity_saved.class);
                        startActivity(intent);
                        break;

                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_category.this, activity_business.class);
                        startActivity(intent);
                        break;


                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

        }

    }


    public void set_profile_pic_tb(){
        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(activity_category.this).load(download_url).into(profile_pic_tb);
            }
        });
    }

    public void search(String keyword){
        /*db.collection(intent.getStringExtra("category").toUpperCase() + "S")
                .whereArrayContains("name", keyword)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pet_zone_logo); //temp pic
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Vet v = new Vet(document.getString("name"), document.getString("address"), (double) document.get("rating"), bitmap, document.get("description").toString());
                        vets_list.add(v);
                        arrange_data arrange_data = new arrange_data(v.getName(), v.getAddress(), v.getPic(), (float) v.getRating(), document.getId(), v.getDescription(), );
                        arrayList.add(arrange_data);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    public void generateDataBlocks(String db_sort) {
        intent = getIntent();
        switch (intent.getStringExtra("category")) {
            case "vet":
                public_values.db.collection("VETS").orderBy(db_sort, Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(public_values.TAG, document.getId() + " => " + document.getData());
                                        Vet v = new Vet(document.getString("name"), document.getString("address"), (double) document.get("rating"), null);
                                        vets_list.add(v);
                                        arrange_data arrange_data = new arrange_data(v.getName(), v.getAddress(), null, (float) v.getRating(),
                                                document.getDouble("num_of_rates").intValue(), document.getId(), document.getReference().getPath(),
                                                document.getString("description"), "VETS", document.getString("pic_id"));
                                        arrayList.add(arrange_data);
                                        adapter.notifyDataSetChanged();
                                    }
                                    System.out.println(arrayList);
                                } else {
                                    Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                break;

            case "pension":
                public_values.db.collection("PENSIONS").orderBy(db_sort)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(public_values.TAG, document.getId() + " => " + document.getData());
                                        Pension p = new Pension(document.getString("name"), document.getString("address"), (double) document.get("rating"), null);
                                        pensions_list.add(p);
                                        arrange_data arrange_data = new arrange_data(p.getName(), p.getAddress(), null, (float) p.getRating(), document.getDouble("num_of_rates").intValue(), document.getId(), document.getReference().getPath(),document.getString("description"), "PENSIONS", document.getString("pic_id"));
                                        arrayList.add(arrange_data);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                break;

            case "salon":
                public_values.db.collection("SALONS").orderBy(db_sort)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(public_values.TAG, document.getId() + " => " + document.getData());
                                        Salon s = new Salon(document.getString("name"), document.getString("address"), (double) document.get("rating"), null);
                                        salons_list.add(s);
                                        arrange_data arrange_data = new arrange_data(s.getName(), s.getAddress(), null, (float) s.getRating(), document.getDouble("num_of_rates").intValue(), document.getId(), document.getReference().getPath(),document.getString("description"), "SALON", document.getString("pic_id"));
                                        arrayList.add(arrange_data);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                break;

            case "walker":
                public_values.db.collection("WALKERS").orderBy(db_sort)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(public_values.TAG, document.getId() + " => " + document.getData());
                                        Walker w = new Walker(document.getString("name"), document.getString("address"), (double) document.get("rating"), null);
                                        walkers_list.add(w);
                                        arrange_data arrange_data = new arrange_data(w.getName(), w.getAddress(),null, (float) w.getRating(), document.getDouble("num_of_rates").intValue(), document.getId(), document.getReference().getPath(), document.getString("description"), "WALKER", document.getString("pic_id"));
                                        arrayList.add(arrange_data);
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    Log.d(public_values.TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                break;
        }
    }
}
