package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class activity_registration extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Intent intent;
    ImageView menu_button;

    ImageView profile_pic;
    business_user business_user;
    Switch business_switch;
    TextInputLayout phone_number;
    TextInputLayout business_info;
    EditText phone_num;
    EditText business_info_text;
    CardView registration_button;
    EditText name;
    EditText address;
    EditText password;
    EditText email;
    private static String collection;
    private static Map<String, Object> new_user = new HashMap<>();
    final static int PICK_IMAGE_REQUEST = 1;
    Uri pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        profile_pic = findViewById(R.id.profile_pic_register);
        profile_pic.setOnClickListener(this);
        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        phone_number = findViewById(R.id.phone_num_layout);
        business_info = findViewById(R.id.business_info_layout);
        business_switch = findViewById(R.id.business_switch);
        business_switch.setOnClickListener(this);
        registration_button = findViewById(R.id.registration_button);
        registration_button.setOnClickListener(this);
        toolbar = findViewById(R.id.app_toolbar);

        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone_num = findViewById(R.id.phone_num);
        business_info_text = findViewById(R.id.business_info);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.Nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){

            /*print message on screen when something on menu pressed (temp option)*/
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.profile:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        onBackPressed();
                        return true;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_registration.this, activity_saved.class);
                        intent.putExtra("activity", "saved");
                        startActivity(intent);
                        finish();
                        return true;
                }
                return true;
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_background_main));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.business_switch:
                if(business_switch.isChecked()){
                    phone_number.setVisibility(View.VISIBLE);
                    business_info.setVisibility(View.VISIBLE);
                    break;
                }
                phone_number.setVisibility(View.GONE);
                business_info.setVisibility(View.GONE);
                phone_num.setText("");
                business_info_text.setText("");
                break;

            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.profile:
                drawerLayout.closeDrawer(GravityCompat.END);
                break;

            case R.id.registration_button:
                /*TODO
                *  check if all fields are correct (half done)*/
                String emailValidate = email.getText().toString();
                String nameValidate =  name.getText().toString();
                String passwordValidate = password.getText().toString();
                String phoneValidate = phone_num.getText().toString();
                String addressValidate = address.getText().toString();
                if(business_switch.isChecked()) {
                    if (Validate(emailValidate, nameValidate, passwordValidate,addressValidate, phoneValidate))
                        register(emailValidate, nameValidate, passwordValidate, addressValidate, phoneValidate);
                } else {
                    if (Validate(emailValidate, nameValidate, passwordValidate, addressValidate, null))
                        register(emailValidate, nameValidate, passwordValidate, addressValidate, null);
                }
                break;

            case R.id.profile_pic_register:
                openFileChooser();
                break;
        }
    }

    public void register(final String email, String name, String password, String address, String phone) {
        if(business_switch.isChecked()) {
            new_user.put("name", name);
            new_user.put("email", email);
            new_user.put("password", password);
            new_user.put("phone", phone);
            new_user.put("address", address);
            new_user.put("is_new", true);
            collection = "BUSINESS_USERS";
            business_user = new business_user(name, email, address, null, null);

            /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(business_user);
            editor.putString("user", json);
            editor.apply();*/
            public_values.current_bUser = business_user;
        }
        else{
            new_user.put("name", name);
            new_user.put("email", email);
            new_user.put("password", password);
            new_user.put("address", address);
            collection = "USERS";
            user user = new user(name, email, address);

            /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(user);
            editor.putString("user", json);
            editor.apply();*/
            public_values.current_user = user;
        }

        public_values.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            public_values.UID = public_values.mAuth.getUid();
                            if(business_switch.isChecked()){
                                public_values.is_business_user = true;
                            }
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(public_values.TAG, "createUserWithEmail:success");
                            public_values.db.collection(collection).document(public_values.UID)
                                    .set(new_user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(public_values.TAG, "Document added successfully ");
                                            System.out.println("user added successfully!!!");
                                            if(business_switch.isChecked()){
                                                  intent = new Intent(activity_registration.this, activity_business_cat.class);
                                                  startActivity(intent);
                                            }
                                            finish();
                                        }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(public_values.TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity_registration.this, "שגיאה\nנסה שוב", Toast.LENGTH_SHORT).show();
                        }
                    }
        });
        uploadImage();
    }

    public boolean Validate(String email, String name, String password, String address, String phone){
        if(business_switch.isChecked())
            return isEmailValid(email) && isNameValid(name) &&
                    isPasswordValid(password) && isAddressValid(address) && isPhoneValid(phone);
        return isEmailValid(email) && isNameValid(name) &&
                isPasswordValid(password);
    }

    public boolean isAddressValid(String address){
        return true;
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPhoneValid(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    public boolean isPasswordValid(String password){
        return password.length() >= 6 && password.length() <= 14;
    }

    public boolean isNameValid(String name){
        return name.matches("^[a-z A-Zא-ת]*$");
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
            Glide.with(this).load(pic).into(profile_pic);
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("מעלה...");
        progressDialog.show();

        //save to storage
        StorageReference storage = public_values.mStorageRef.getReference().child("profilePics/" + public_values.UID);
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

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
