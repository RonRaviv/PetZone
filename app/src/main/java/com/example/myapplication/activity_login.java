package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class activity_login extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu_button;
    Intent intent;
    TextView link;
    EditText email;
    EditText password;
    CardView login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        link = findViewById(R.id.registration_link);
        link.setOnClickListener(this);
        toolbar = findViewById(R.id.app_toolbar);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.Nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        return true;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_login.this, activity_saved.class);
                        startActivity(intent);
                        finish();
                        return true;
                }
                return true;
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_background_main));

        intent = getIntent();
        if (intent.getStringExtra("activity") == "saved")
            finish();


    }

   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        *//*FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);*//*
    }*/

    //Change UI according to user data.
    public void  updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"התחברת בהצלחה",Toast.LENGTH_SHORT).show();
            /*intent = new Intent(activity_login.this, MainActivity.class);
            startActivity(intent);*/
            finish();
        }else {
            Toast.makeText(this,"התחברות נכשלה",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;
            case R.id.registration_link:
                intent = new Intent(activity_login.this, activity_registration.class);
                intent.putExtra("activity", "registration");
                startActivity(intent);
                finish();
                break;
            case R.id.login_button:
                login();
                break;
        }
    }

    public void login(){
        final String email_str = email.getText().toString();
        String password_str = password.getText().toString();

        public_values.mAuth.signInWithEmailAndPassword(email_str, password_str)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(public_values.TAG, "signInWithEmail:success");

                            //public_values.user_email = email_str;

                            get_user_type();

                            updateUI(public_values.mAuth.getCurrentUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(public_values.TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    public void get_user_type(){
        public_values.db.collection("BUSINESS_USER")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (doc.getId().equals(public_values.UID)){
                                public_values.is_business_user = true;
                                break;
                            }
                        }
                        get_user_data();
                    }
                });
    }

    public void get_user_data(){
        String coll;
        if(public_values.is_business_user){
            coll = "BUSINESS_USERS";
        }
        else{
            coll = "USERS";
        }
        System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ"+coll);
        System.out.println(email);
        System.out.println(email.getText());
        System.out.println("55555555555555555555555555555"+email.getText().toString());
        public_values.db.collection(coll).document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot doc) {
            FirebaseUser user = public_values.mAuth.getCurrentUser();
            if(public_values.is_business_user) {
                public_values.current_bUser = new business_user(doc.getString("name"), doc.getString("email"), doc.getString("address"),doc.getDocumentReference("business_page"), doc.getString("category"));
                System.out.println("222222222222222222222222222222222" + public_values.current_user);
                //save_to_device();
                //updateUI(user);
            }
            else{
                public_values.current_user = new user(doc.getString("name"), doc.getString("email"), doc.getString("address"));
                System.out.println("222222222222222222222222222222222" + public_values.current_user);
                //save_to_device();
                //updateUI(user);
            }


            }
        });
    }

    public void save_to_device(){
        System.out.println("99999999999999999999999999999999"+public_values.current_user);
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json;
        if(public_values.is_business_user){
            json = gson.toJson(public_values.current_bUser);
        }
        else {
            json = gson.toJson(public_values.current_user);
        }
        editor.putString("user", json);
        editor.apply();
    }
}
