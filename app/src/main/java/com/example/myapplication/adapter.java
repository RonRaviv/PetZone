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
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class adapter extends ArrayAdapter<arrange_data>{
    private Context context;
    private List<arrange_data> objects;
    private Activity activity;
    Intent intent;
    private String category;

    public adapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<arrange_data> objects, Activity activity) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        this.activity = activity;
    }

    @Override
    public View getView(final int Position, View view, final ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        //set the view of the block
        view = layoutInflater.inflate(R.layout.data_layout, parent, false);
        //find views in the block
        final ImageView image = view.findViewById(R.id.place_picture);
        TextView name = view.findViewById(R.id.place_name);
        TextView address = view.findViewById(R.id.place_address);
        RatingBar rating = view.findViewById(R.id.ratingBar);
        CardView cardView = view.findViewById(R.id.block);
        final TextView doc_id = view.findViewById(R.id.doc_id);
        final TextView doc_ref = view.findViewById(R.id.doc_ref);

        //set values to the block's views
        final arrange_data temp = objects.get(Position);
        name.setText(temp.getName());
        address.setText(temp.getAddress());
        rating.setRating(temp.getRating());
        doc_id.setText(temp.getId());
        doc_ref.setText(temp.getDocRef());
        category = temp.getCategory();
        String pic_id = temp.getPic_id();
        cardView.setContentDescription(temp.getId() + " " + temp.getDocRef());


        //create storage reference
        StorageReference storageReference = public_values.mStorageRef.getReference().child("businessPics/" + pic_id);

        //get pic
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //if success save to block pic
                String download_url = uri.toString();
                Glide.with(activity).load(download_url).fallback(R.drawable.ic_no_profile_photo).into(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(activity, detailed_info.class);
                intent.putExtra("id", v.getContentDescription().toString().split(" ")[0]);
                intent.putExtra("category", activity.getLocalClassName());
                intent.putExtra("cat", category);
                intent.putExtra("ref", v.getContentDescription().toString().split(" ")[1]);
                context.startActivity(intent);

            }
        });

        /*cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(activity.getLocalClassName() == "activity_saved"){
                    SharedPreferences sharedPreferences = context.getSharedPreferences(public_values.SHARED_PREFS_SAVED, MODE_PRIVATE);
                    Gson gson = new Gson();
                    String json = sharedPreferences.getString(public_values.SAVED_LIST, null);
                    Type type = new TypeToken<ArrayList<arrange_data>>(){}.getType();
                    ArrayList<arrange_data> temp_saved_list = gson.fromJson(json, type);
                    for (arrange_data obj:objects) {
                        if(obj.getId() == doc_id.getText()){
                            temp_saved_list.remove(obj);
                        }
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    json = gson.toJson(temp_saved_list);
                    editor.putString(public_values.SAVED_LIST, json);
                    editor.apply();
                    adapter.super.notifyDataSetChanged();
                }
                return true;
            }
        });*/

        return view;
    }
}
