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

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Profile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import com.cs501.project.Model.Color;
import com.cs501.project.Model.Jacket;
import com.cs501.project.Model.Pants;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FireBaseManager fb_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toAdd = (Button) findViewById(R.id.button4);
        Button settings = (Button) findViewById(R.id.button6);

        //Load clothes for future use
        fb_manager = FireBaseManager.getInstance();
        fb_manager.getClothes();

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

        /*
        FileInputStream serviceAccount = new FileInputStream("./final-project-c05e5-firebase-adminsdk-hstj7-eab049ff7b.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://final-project-c05e5-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        */

        // should be login now
//        fb_manager = FireBaseManager.getInstance();
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

            // calls login
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        }
    }
}