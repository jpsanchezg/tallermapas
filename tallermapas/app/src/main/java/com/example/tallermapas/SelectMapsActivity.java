package com.example.tallermapas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SelectMapsActivity extends AppCompatActivity {
    Button bntgoogle,btnMapbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_maps);
        bntgoogle = findViewById(R.id.googlemapsBTN);
        btnMapbox = findViewById(R.id.mapboxBTN);
        bntgoogle.setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
        });
        btnMapbox.setOnClickListener(v -> {
            //startActivity(new Intent(this, MapboxActivity.class));
        });


    }
}