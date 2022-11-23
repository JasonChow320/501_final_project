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

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Profile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toAdd = (Button) findViewById(R.id.button4);

        // to get current user
        mAuth = FirebaseAuth.getInstance();
        this.user = new Profile();

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
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
        });

        /*
        FileInputStream serviceAccount = new FileInputStream("./final-project-c05e5-firebase-adminsdk-hstj7-eab049ff7b.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://final-project-c05e5-default-rtdb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
        */

        // calls login
        Intent i = new Intent(MainActivity.this, Login.class);
        startActivity(i);

        // should be login now
        getUser();
    }

    public void getUser(){

        // retrieve account database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "Signed in as user: " + currentUser.getUid());

        // Read from the database
        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                Profile account = dataSnapshot.getValue(Profile.class);
                if(account != null){
                    Log.d(TAG, "Value is: " + account.toString());
                }
                initalizeUser(account);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void initalizeUser(Profile profile){
        if(profile == null){
            return;
        }
        this.user = profile;
        Log.d(TAG, "This is stored in Main Activity! Info: " + this.user.toString());
    }
}