package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs501.project.Model.Color;
import com.cs501.project.Model.FireBaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FireBaseManager fb_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toViewOutfit = (Button) findViewById(R.id.button3);
        Button toAdd = (Button) findViewById(R.id.button4);
        Button settings = (Button) findViewById(R.id.main_setting_button);
        Button back = (Button) findViewById(R.id.main_back_button);
        // if we want to use User's data here
        fb_manager = FireBaseManager.getInstance();

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, settings.class);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        toViewOutfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewOutfit.class);
                startActivity(i);
            }
        });
    }

    // reload the user credential everytime
    @Override
    public void onStart() {

        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            currentUser.reload();
            Log.d(TAG, "currentUser is not null!");
            // continue with the app
        } else {

            // this is bad :( user's not logged in but we're in the main application
            Toast.makeText(MainActivity.this, "Unable to retrieve user data. Please try again",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
