package com.cs501.project.Model;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    The FireBaseManager class controls the data flow between the Realtime database
    and the application profile's class.
    This class uses the Singleton design pattern
 */
public class FireBaseManager {

    // create private Singleton object
    private static FireBaseManager manager_instance;

    // private methods
    private final static String TAG = "FireBaseManager";

    // Initialize Firebase Auth and Profile
    private FirebaseAuth mAuth;
    private Profile user;
    private int user_idx;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;

    // private constructor only for getInstance
    private FireBaseManager() {

        mAuth = FirebaseAuth.getInstance();
        this.user = new Profile();

        // retrieve account database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts");
        currentUser = mAuth.getCurrentUser();

        user_idx = 0;

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
                user = account;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // static get instance method
    public static FireBaseManager getInstance() {
        if (manager_instance == null) {
            manager_instance = new FireBaseManager();
        }
        return manager_instance;
    }

    public Profile getProfile(){
        return this.user;
    }

    public void setProfile(Profile user){
        if(user == null){
            return;
        }

        this.user = user;
        return;
    }

    public void addClothes(Clothes new_clothes){

        if(new_clothes == null){
            Log.d(TAG, "Unable to add null object to wardrobe");
            return;
        }
        Log.d(TAG, "Adding clothes: " + new_clothes.toString());

        Wardrobe user_wardrobe = user.getUsers().get(user_idx).getWardrobe();
        user_wardrobe.insertClothes(new_clothes);

        myRef.child(currentUser.getUid()).setValue(user);

        return;
    }

    public ArrayList<Clothes> getClothes(){

        ArrayList<User> users = user.getUsers();
        Log.d(TAG, "Getting clothes from FireBaseManager: user_idx: " + user_idx + ", users size: " + users.size());

        if(users.size() <= user_idx){
            return null;
        }

        User user = users.get(user_idx);
        Wardrobe user_wardrobe = user.getWardrobe();
        ArrayList<Clothes> clothes = user_wardrobe.getClothes();

        Log.d(TAG, "The clothes is: " + clothes);
        return clothes;
    }
}
