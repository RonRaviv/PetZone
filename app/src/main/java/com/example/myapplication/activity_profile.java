package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class activity_profile extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser auth_user = FirebaseAuth.getInstance().getCurrentUser();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Uri filePath;
    Intent intent;
    ImageView profile_pic;
    ImageView menu_button;
    ImageView edit_name;
    ImageView edit_password;
    CardView logout_button;
    ImageView confirm_edit_name;
    ImageView confirm_edit_password;
    ImageView cancel_edit_name;
    ImageView cancel_edit_password;
    EditText edit_name_text;
    EditText edit_password_text;
    Drawable original_editText;
    CardView add_photo;
    Drawable pic;

    String current_name;
    String current_password;
    ImageView profile_pic_tb;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static  final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_pic = findViewById(R.id.profile_pic);
        edit_name = findViewById(R.id.name_edit);
        edit_name.setOnClickListener(this);
        edit_password = findViewById(R.id.password_edit);
        edit_password.setOnClickListener(this);
        logout_button = findViewById(R.id.logout_button);
        logout_button.setOnClickListener(this);
        confirm_edit_name = findViewById(R.id.name_edit_confirm);
        confirm_edit_name.setOnClickListener(this);
        confirm_edit_password = findViewById(R.id.password_edit_confirm);
        confirm_edit_password.setOnClickListener(this);
        cancel_edit_name = findViewById(R.id.name_edit_cancel);
        cancel_edit_name.setOnClickListener(this);
        cancel_edit_password = findViewById(R.id.password_edit_cancel);
        cancel_edit_password.setOnClickListener(this);
        edit_name_text = findViewById(R.id.name_text_edit);
        edit_password_text = findViewById(R.id.password_text_edit);
        add_photo = findViewById(R.id.add_photo);
        add_photo.setOnClickListener(this);
        profile_pic_tb = findViewById(R.id.profile_pic_tb);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(this);
        drawerLayout = findViewById(R.id.category_drawer_layout);
        navigationView = findViewById(R.id.category_nav_view);
        if(public_values.mAuth.getCurrentUser() != null) {
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
                        return true;

                    case R.id.saved:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_profile.this, activity_saved.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.business_page:
                        drawerLayout.closeDrawer(GravityCompat.END);
                        intent = new Intent(activity_profile.this, activity_business.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                return true;
            }
        });

        //set to uneditable
        edit_name_text.setTag(edit_name_text.getKeyListener());
        edit_name_text.setKeyListener(null);
        edit_password_text.setTag(edit_password_text.getKeyListener());
        edit_password_text.setKeyListener(null);
        //change and save background (no line)
        original_editText = edit_name_text.getBackground();
        edit_name_text.setBackground(null);
        edit_password_text.setBackground(null);

        //set name and password to user data from database
        SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);
        edit_name_text.setText(sharedPreferences.getString("user_name","אורח"));
        public_values.user_email = public_values.mAuth.getCurrentUser().getEmail();
        public_values.UID = public_values.mAuth.getUid();
        DocumentReference docRef = public_values.db.collection("USERS").document(public_values.UID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                edit_password_text.setText(documentSnapshot.getString("password"));
            }
        });

        //save current user info in case of edit canceled
        current_name = edit_name_text.getText().toString();
        current_password = edit_password_text.getText().toString();

        //System.out.println(public_values.current_user.getProfile_pic());
        if(public_values.mAuth.getCurrentUser() != null){
            if(public_values.is_business_user) {
                public_values.db.collection("BUSINESS_USERS").document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        public_values.current_bUser = new business_user(doc.getString("name"), doc.getString("email"), doc.getString("address"), doc.getDocumentReference("page"), doc.getString("category"));
                        get_user_data();
                    }
                });
            }
            else {
                public_values.db.collection("BUSINESS_USERS").document(public_values.UID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        public_values.current_user = new user(doc.getString("name"), doc.getString("email"), doc.getString("address"));
                        get_user_data();
                    }
                });
            }


            set_profile_pic_tb();
            System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHH");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.END);
                break;

            case R.id.name_edit:
                if (edit_password.getVisibility() == View.GONE)
                    cancel_edit_password.callOnClick();
                edit_name.setVisibility(View.GONE);
                confirm_edit_name.setVisibility(View.VISIBLE);
                cancel_edit_name.setVisibility(View.VISIBLE);
                edit_name_text.setKeyListener((KeyListener) edit_name_text.getTag());
                edit_name_text.setBackground(original_editText);
                break;

            case R.id.password_edit:
                if(edit_name.getVisibility() == View.GONE)
                    cancel_edit_name.callOnClick();
                edit_password.setVisibility(View.GONE);
                confirm_edit_password.setVisibility(View.VISIBLE);
                cancel_edit_password.setVisibility(View.VISIBLE);
                edit_password_text.setKeyListener((KeyListener) edit_name_text.getTag());
                edit_password_text.setBackground(original_editText);
                break;

            case R.id.name_edit_confirm:
                edit_password.setVisibility(View.VISIBLE);
                confirm_edit_password.setVisibility(View.GONE);
                cancel_edit_password.setVisibility(View.GONE);
                String new_name = edit_name_text.getText().toString();
                current_name = new_name;
                if(public_values.is_business_user){
                    public_values.current_bUser.setName(current_name);
                }
                else {
                    public_values.current_user.setName(current_name);
                }
                DocumentReference user;
                if(public_values.is_business_user){
                    user = public_values.db.collection("BUSINESS_USERS").document(public_values.user_email);

                }
                else{
                    user = public_values.db.collection("USERS").document(public_values.user_email);

                }
                user.update("name", new_name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(public_values.TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(public_values.TAG, "Error updating document", e);
                        }
                    });
                /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
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
                editor.apply();*/

            case R.id.name_edit_cancel:
                edit_name.setVisibility(View.VISIBLE);
                confirm_edit_name.setVisibility(View.GONE);
                cancel_edit_name.setVisibility(View.GONE);
                edit_name_text.setBackground(null);
                edit_name_text.setTag(edit_name_text.getKeyListener());
                edit_name_text.setKeyListener(null);
                edit_name_text.setText(current_name);
                hideKeyBoard();
                break;

            case R.id.password_edit_confirm:
                edit_password.setVisibility(View.VISIBLE);
                confirm_edit_password.setVisibility(View.GONE);
                cancel_edit_password.setVisibility(View.GONE);
                String new_password = edit_password_text.getText().toString();
                auth_user.updatePassword(new_password)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(public_values.TAG, "User password updated.");
                                }
                            }
                        });
                DocumentReference user1;
                if(public_values.is_business_user){
                    user1 = public_values.db.collection("BUSINESS_USERS").document(public_values.user_email);
                }
                else{
                    user1 = public_values.db.collection("USERS").document(public_values.user_email);

                }
                user1.update("name", new_password)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(public_values.TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(public_values.TAG, "Error updating document", e);
                            }
                        });

            case R.id.password_edit_cancel:
                edit_password.setVisibility(View.VISIBLE);
                confirm_edit_password.setVisibility(View.GONE);
                cancel_edit_password.setVisibility(View.GONE);
                edit_password_text.setBackground(null);
                edit_password_text.setTag(edit_password_text.getKeyListener());
                edit_password_text.setKeyListener(null);
                edit_password_text.setText(current_password);
                //hideKeyBoard();
                break;

            /*case R.id.email:
                user.updateEmail("user@example.com")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                }
                            }
                        });
                break;*/

            case R.id.add_photo:
                add_photo(v);
                break;

            case R.id.logout_button:
                //delete all data saved on device
                /*SharedPreferences sharedPreferences1 = getSharedPreferences("user", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear().apply();*/
                SharedPreferences sharedPreferences1 = getSharedPreferences(public_values.SHARED_PREFS_SAVED, MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.clear().apply();

                public_values.mAuth.signOut();
                Toast.makeText(activity_profile.this,"התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    private void add_photo(View v){
        PopupMenu attachFilePopup = new PopupMenu(this, v , Gravity.BOTTOM);
        attachFilePopup.inflate(R.menu.photo_menu);
        attachFilePopup.show();
        attachFilePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.upload_pic:
                        openFileChooser();
                        break;

                    case R.id.delete_pic:
                        delete_pic();
                        break;
                }
                return false;
            }
        });
    }

    public void delete_pic(){
        //delete from device storage
        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        if(public_values.is_business_user) {
            Type type = new TypeToken<user>() {}.getType();
            business_user user = gson.fromJson(json, type);
            user.setProfile_pic(null);
            public_values.current_bUser = user;
            json = gson.toJson(user);
        }
        else {
            Type type = new TypeToken<business_user>(){}.getType();
            user user = gson.fromJson(json, type);
            user.setProfile_pic(null);
            public_values.current_user = user;
            json = gson.toJson(user);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", json);
        editor.apply();*/


        //delete from storage database
        public_values.mStorageRef.getReference().child("profilePics").child(public_values.UID).delete();
    }

    private void openFileChooser() {
        //open the phone's files app
        Intent intent = new Intent();
        intent.setType("image/*"); //get only images
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //react to result for activity
        super.onActivityResult(requestCode, resultCode, data);
        //check if result is picture
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            filePath = data.getData();
            Glide.with(this).load(filePath).fallback(R.drawable.ic_no_profile_photo)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            pic = resource;
                            uploadImage();
                            return false;
                        }
                    })
                    .into(profile_pic);
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("מעלה...");
        progressDialog.show();

        //save to storage
        StorageReference storage = public_values.mStorageRef.getReference().child("profilePics/" + public_values.UID);
        storage.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                });


        //save to user's device
        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type;
        if(public_values.is_business_user){
            type = new TypeToken<business_user>(){}.getType();
            business_user user = gson.fromJson(json, type);
            if(user == null){
                System.out.println("////////////////////////////////////////////////"+public_values.current_bUser);
                if(public_values.current_bUser == null){

                }
                user = public_values.current_bUser;
            }
            user.setProfile_pic(pic);
            json = gson.toJson(user);
        }
        else {
            type = new TypeToken<user>() {}.getType();
            user user = gson.fromJson(json, type);
            if(user == null){
                user = public_values.current_user;
            }
            user.setProfile_pic(pic);
            json = gson.toJson(user);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", json);
        editor.apply();*/

    }

    public void get_user_data(){
        /*SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user", null);
        Type type;
        if(public_values.is_business_user){
            type = new TypeToken<business_user>(){}.getType();
            public_values.current_bUser = gson.fromJson(json, type);
        }
        else {
            type = new TypeToken<user>(){}.getType();
            public_values.current_user = gson.fromJson(json, type);
        }*/
        System.out.println("................bbbb....................." + public_values.current_user);
        try{
            if(public_values.is_business_user){
                profile_pic_tb.setImageDrawable(public_values.current_bUser.getProfile_pic());
                profile_pic.setImageDrawable(public_values.current_bUser.getProfile_pic());
                edit_name_text.setText(public_values.current_bUser.getName());
            }
            else {
                profile_pic_tb.setImageDrawable(public_values.current_user.getProfile_pic());
                profile_pic.setImageDrawable(public_values.current_user.getProfile_pic());
                edit_name_text.setText(public_values.current_user.getName());
            }
        }
        catch (NullPointerException e){
            profile_pic_tb.setImageResource(R.drawable.ic_no_profile_photo);
            profile_pic.setImageResource(R.drawable.ic_no_profile_photo);
            edit_name_text.setText("");
        }

    }

    public void set_profile_pic_tb(){
        StorageReference ref = public_values.mStorageRef.getReference().child("profilePics/" + public_values.mAuth.getCurrentUser().getUid());
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(activity_profile.this).load(download_url).into(profile_pic_tb);
            }
        });
    }

    public void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
