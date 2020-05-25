package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class comment_adapter extends ArrayAdapter<arrange_comment_data>{
    private Context context;
    private List<arrange_comment_data> objects;
    private Activity activity;
    Intent intent;

    public comment_adapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<arrange_comment_data> objects, Activity activity) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.activity = activity;
    }

    @Override
    public View getView(int Position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        view = layoutInflater.inflate(R.layout.comment_layout, parent, false);
        final ImageView image = view.findViewById(R.id.publisher_picture);
        TextView name = view.findViewById(R.id.publisher_name);
        TextView date = view.findViewById(R.id.publish_date);
        RatingBar rating = view.findViewById(R.id.comment_ratingBar);
        TextView comment = view.findViewById(R.id.comment_text);
        //CardView cardView = view.findViewById(R.id.comment_block);

        arrange_comment_data temp = objects.get(Position);
        name.setText(temp.getName());
        date.setText(temp.getDate());
        rating.setRating(temp.getRating());
        comment.setText(temp.getComment_text());
        //image.setImageDrawable(temp.getPicture());


        StorageReference storageReference = public_values.mStorageRef.getReference().child("profilePics/" + temp.getUser_id());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String download_url = uri.toString();
                Glide.with(activity).load(download_url).fallback(R.drawable.ic_no_profile_photo).error(R.drawable.ic_no_profile_photo).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });


        return view;
    }
}
