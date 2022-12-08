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
import com.cs501.project.Model.User_settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private FireBaseManager fb_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(FireBaseManager.getInstance().getUser().getUserSettings().getLanguage());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toWardrobe = (Button) findViewById(R.id.button1);
        Button toGeneration = (Button) findViewById(R.id.button2);
        Button toViewOutfit = (Button) findViewById(R.id.button3);
        Button toAdd = (Button) findViewById(R.id.button4);
        Button settings = (Button) findViewById(R.id.main_setting_button);
        Button back = (Button) findViewById(R.id.main_back_button);
        Button toggle_language = (Button) findViewById(R.id.toggle_language);
        // if we want to use User's data here

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

        toggle_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb_manager = FireBaseManager.getInstance();
                String current = fb_manager.getUser().getUserSettings().getLanguage();
                String opposite = (current.equals("en")) ? "es" : "en";
                fb_manager.updateLanguage(opposite);
                Locale locale = new Locale(opposite);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                config.setLocale(locale);
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                recreate();
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
            Toast.makeText(MainActivity.this, "Unable to retrieve user data. Please try again",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
