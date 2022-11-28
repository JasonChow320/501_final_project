package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.Profile;
import com.cs501.project.Model.RandomString;
import com.cs501.project.Model.SQLDataBase;
import com.cs501.project.Model.User;
import com.cs501.project.Model.Wardrobe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username, password;
    private Button sign_up, sign_out_button;
    private CheckBox password_protected_checkbox;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        this.username = (EditText) findViewById(R.id.username_field);
        this.password = (EditText) findViewById(R.id.password_field);
        this.sign_up = (Button) findViewById(R.id.button_signup);
        this.sign_out_button = (Button) findViewById(R.id.login_sign_out_button);

        this.password_protected_checkbox = (CheckBox) findViewById(R.id.password_protected_checkbox);

        // TODO create user profiles for each user
        this.sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO add create user feature
                //createUser(username.getText().toString(), password.getText().toString());

                // TODO remove this
                // calls signup
                Intent i = new Intent(Login.this, MainActivity.class);
                startActivity(i);
            }
        });

        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            // Error! We can only get here if a user logins!!
            Log.d(TAG, "Don't have a FireBase User, go back to SignIn");
            finish();
        }
    }

    private void createUser(String username, String password) {
        // TODO
    }
}