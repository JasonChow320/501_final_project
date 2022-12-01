package com.cs501.project;

import static android.content.Context.MODE_PRIVATE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.data.ApnSetting;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.FireBaseManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button sign_up, sign_out_button;
    private FireBaseManager fb_manager;

    private static final String TAG = "LoginActivity";

    private ListView lvUsers;     //Reference to the listview GUI component
    LoginCustomAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Request for permissions
        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {  });

        locationPermissionRequest.launch(new String[] { //request location permissions
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        this.sign_up = (Button) findViewById(R.id.button_signup);
        this.sign_out_button = (Button) findViewById(R.id.login_sign_out_button);

        // Get account's users
        //fb_manager = FireBaseManager.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // retrieve account database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "Signed in as user: " + currentUser.getUid());

        // Initialize our firebase manager
        fb_manager = FireBaseManager.getInstance();

        // Read from the database
        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                Profile profile = dataSnapshot.getValue(Profile.class);

                // update list
                lvUsers = (ListView)findViewById(R.id.profile_list);
                lvAdapter = new LoginCustomAdapter(Login.this, profile.getUsers());
                lvUsers.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // TODO create user profiles for each user
        this.sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO add create user feature
                Intent i = new Intent(Login.this, MakeProfile.class);
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
}

// custom adapter
class LoginCustomAdapter extends BaseAdapter {

    ArrayList<User> users;
    SharedPreferences sharedPreferences;
    Context context;

    public LoginCustomAdapter(Context aContext, ArrayList<User> users) {
        //initializing our data in the constructor.
        context = aContext;

        if(users == null || users.size() <= 0){
            this.users = new ArrayList<User>();
        } else{
            this.users = users;
        }

        sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.login_list_view, parent, false);
        } else {
            row = convertView;
        }

        TextView username = (TextView) row.findViewById(R.id.login_username_field);
        Button signin = (Button) row.findViewById(R.id.login_signin_button);
        Button edit = (Button) row.findViewById(R.id.login_edit_button);
        Button delete = (Button) row.findViewById(R.id.login_delete_button);

        User user = users.get(position);

        // Button onClicks
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set the FireBaseManager to use our user's info
                FireBaseManager fb_manager = FireBaseManager.getInstance();
                fb_manager.setUserIdx(position);

                Log.d("LoginListView", "Clicked Sign IN");

                // login!
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
            }
        });

        // everything we add to view should be in the try catch
        try {
            username.setText(user.getUsername());
        } catch (Exception e){
            Toast.makeText(context, "Unable to parse data for the " + position + " user. Please try again",
                    Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sh = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return row;
    }
}