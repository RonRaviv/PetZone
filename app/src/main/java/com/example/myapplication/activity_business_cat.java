package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class activity_business_cat extends AppCompatActivity implements View.OnClickListener {

    Intent intent;
    ImageView menu_button;
    ImageView vet;
    ImageView salon;
    ImageView pension;
    ImageView walker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_cat);
        intent = getIntent();
        menu_button = findViewById(R.id.menu_button);
        menu_button.setVisibility(View.GONE);
        vet = findViewById(R.id.vet);
        vet.setOnClickListener(this);
        salon = findViewById(R.id.salon);
        salon.setOnClickListener(this);
        pension = findViewById(R.id.pension);
        pension.setOnClickListener(this);
        walker = findViewById(R.id.dog_walker);
        walker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(activity_business_cat.this, activity_business.class);
        switch (v.getId()){
            case R.id.vet:
                intent.putExtra("business_cat", "vets");
                break;
            case R.id.salon:
                intent.putExtra("business_cat", "salons");
                break;
            case R.id.pension:
                intent.putExtra("business_cat", "pensions");
                break;
            case R.id.dog_walker:
                intent.putExtra("business_cat", "walkers");
                break;
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}

