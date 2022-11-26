package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cs501.project.Model.Color;
import com.cs501.project.Model.Jacket;
import com.cs501.project.Model.Pants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toAdd = (Button) findViewById(R.id.button4);
        Button settings = (Button) findViewById(R.id.button6);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, settings.class);
                startActivity(i);
            }
        });

        toWardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewWardrobe.class);
                startActivity(i);
            }
        });

        toGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, GenerateOutfit.class);
                startActivity(i);
            }
        });

        toAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddToWardrobe.class);
                startActivity(i);
            }
        });

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {  });

        locationPermissionRequest.launch(new String[] { //request location permissions
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

    }
}