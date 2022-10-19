package com.example.tallermapas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageButton btnMapa, btnImagenes;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnMapa = findViewById(R.id.btnMapa);
        btnImagenes = findViewById(R.id.btnImagenes);


        btnMapa.setOnClickListener(v -> {
            startActivity(new Intent(this, SelectMapsActivity.class));
        });
        btnImagenes.setOnClickListener(v -> {
            startActivity(new Intent(this, ImagenesActivity.class));
        });
    }

}