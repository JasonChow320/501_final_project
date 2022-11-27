package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button sign_up;
    private CheckBox password_protected_checkbox;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference storageRef;

    private SQLDataBase sql_database;
    private String accountId;
    private final static String ACC_PASSWORD = "th3r00t";

    private static final String TAG = "LoginActivity";

    /*
    The flow of the login is as follows:
    1) check if the device is "registered" with the database (check sql for entry of account id)
        1.1) if not, then register device to firebase
    2) use account info from SQL database to login
    3) access account data which is a profile class
    */

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

        // get sql database to check if we have an account with the firebase database
        sql_database = new SQLDataBase(this);

        // FOr testing only - this creates a new account everytime
        //sql_database.onUpgrade();

        ArrayList<String> account_id = sql_database.selectAll();

        // make account if new device
        if(account_id.size() <= 0){

            // don't have an account, need to make one
            Log.d(TAG, "Need to make new account");

            // create account
            this.accountId = RandomString.getAlphaNumericString(16);
            this.accountId += "@gmail.com";

            sql_database.insert(accountId);

            this.createAccount(this.accountId, ACC_PASSWORD);
        } else {

            // have account, login
            Log.d(TAG, "Found account: " + account_id.get(0));
            Log.d(TAG, "number of account ids: " + account_id.size());

            mAuth.signInWithEmailAndPassword(account_id.get(0), ACC_PASSWORD)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                signInSuccessful();
                                finish();
                            } else {
                                // If sign in fails, display a message to the user
                                Log.d(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }

        // TODO create user profiles for each device
        this.sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(username.getText().toString(), password.getText().toString());
            }
        });
    }

    public void signInSuccessful(){

        // retrieve account database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts");
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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
            // TODO could be a bug since we're auto-logging in
            Log.d(TAG, "Already have a user, log in!");
            finish();
        }
    }

    // create account in our authentication server
    private void createAccount(String email, String password) {

        // TODO add input checking

        Log.d(TAG, "Creating authentication account");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addAccount(user, email);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser(String username, String password) {
        // TODO
    }

    // add account to our database for storage
    private void addAccount(FirebaseUser user, String username) {

        Log.d(TAG, "Calling addAccount");
        if(user == null || username == null){

            // error
            return;
        }

        // get database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Profile profile = new Profile();
        profile.setAccountEmail(username);
        profile.setUserId(user.getUid());

        // TODO allow users to create User class themselves
        User new_user = new User();
        new_user.setUserId(RandomString.getAlphaNumericString(16));
        profile.addUser(new_user);

        Clothes_Factory factory = new Clothes_Factory();
        Wardrobe wardrobe = new_user.getWardrobe();
        wardrobe.insertClothes(factory.get_tshirt());

        myRef.child("accounts").child(profile.getUserId()).setValue(profile);

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