package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.Profile;
import com.cs501.project.Model.User;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText username, password;
    private Button sign_up;
    private CheckBox password_protected_checkbox;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageRef;

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
        this.password_protected_checkbox = (CheckBox) findViewById(R.id.password_protected_checkbox);

        this.sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(username.getText().toString(), password.getText().toString());
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user = ref.child("users");

        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile users = dataSnapshot.getValue(Profile.class);

                if(users == null){
                    return;
                }
                Log.d(TAG, Arrays.toString(users.getUsers().toArray()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void createAccount(String username, String password) {

        // TODO add input checking

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUser(user, username);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            addUser(null, null);
                        }
                    }
                });
    }

    private void addUser(FirebaseUser user, String username) {

        if(user == null || username == null){
            this.username.setText("");
            this.password.setText("");

            return;
        }

        // get database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Create a Cloud Storage reference from the app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        User user_login = new User(user.getUid(), username);

        myRef.child("users").setValue(user_login);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Value is: " + user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        /* add this if we want to protect wardrobe
            "wardrobe": {
              "$userId": {
                // grants write access to the owner of this user account
                // whose uid must exactly match the key ($userId)
                ".write": "$userId === auth.uid",
                ".read": "$userId === auth.uid"
              }
            },
         */
    }
}