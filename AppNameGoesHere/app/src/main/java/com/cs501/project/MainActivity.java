package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Profile;
import com.cs501.project.Model.User_settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FireBaseManager fb_manager;
    private boolean first_boot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(FireBaseManager.getInstance().getUser().getUserSettings().getLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        this.first_boot = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toViewOutfit = (Button) findViewById(R.id.button3);
        Button toAdd = (Button) findViewById(R.id.button4);
        Button settings = (Button) findViewById(R.id.main_setting_button);
        Button back = (Button) findViewById(R.id.main_back_button);

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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // retrieve account database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Update if language changes
        FireBaseManager fb_manager = FireBaseManager.getInstance();
        myRef.child(currentUser.getUid()).child("users").child(String.valueOf(fb_manager.getUserIdx())).child("userSettings").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                Profile account = dataSnapshot.getValue(Profile.class);
                if(account != null){
                    Log.d(TAG, "Value is: " + account.toString());
                }
                if(first_boot) {
                    first_boot = false;
                } else {
                    recreate();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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
        fb_manager = FireBaseManager.getInstance();
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        ConstraintLayout con = findViewById(R.id.background);
        String backgroundColor = getResources().getStringArray(R.array.themesValues)[uSettings.getTheme()];
        System.out.println(backgroundColor);
        con.setBackgroundColor(Color.parseColor(backgroundColor));

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            currentUser.reload();
            Log.d(TAG, "currentUser is not null!");
            // continue with the app
        } else {

            // this is bad :( user's not logged in but we're in the main application
            Toast.makeText(MainActivity.this, getResources().getString(R.string.fail_user_data),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
