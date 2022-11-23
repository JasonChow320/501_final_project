package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.cs501.project.Model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewWardrobe extends AppCompatActivity {

    private final static String TAG = "ViewWardrobe";
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Profile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wardrobe);
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