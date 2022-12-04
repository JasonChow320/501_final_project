package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Profile;
import com.cs501.project.Model.User;
import com.cs501.project.Model.User_settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends AppCompatActivity {

    TextView oneLayerTemp;
    TextView twoLayerTemp;
    TextView threeLayerTemp;
    TextView flashMode;
    int currOneTemp;
    int currThreeTemp;

    private FireBaseManager fb_manager;
    private final static String TAG = "SettingActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //fb_manager = FireBaseManager.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // retrieve account database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "Signed in as user: " + currentUser.getUid());

        // Initialize our firebase manager
        fb_manager = FireBaseManager.getInstance();
        User_settings uSettings = fb_manager.getUser().getUserSettings();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new settingsFragment())
                .commit();

        oneLayerTemp = (TextView) findViewById(R.id.textView6);
        threeLayerTemp = (TextView) findViewById(R.id.textView7);
        flashMode = (TextView) findViewById(R.id.textView4);

        // Settings auto updates when database changes
        myRef.child(currentUser.getUid()).child("users").child(String.valueOf(fb_manager.getUserIdx())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                User user = dataSnapshot.getValue(User.class);

                User_settings setting = user.getUserSettings();
                oneLayerTemp.setText(String.valueOf(setting.getOneLayerTemp()) + "F");
                threeLayerTemp.setText(String.valueOf(setting.getThreeLayerTemp()) + "F");
                flashMode.setText(String.valueOf(setting.getFlashMode()));

            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        currOneTemp = 0;
        currThreeTemp = 0;
        int t = R.xml.settings;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        oneLayerTemp.setText(uSettings.getOneLayerTemp() +" F");
        threeLayerTemp.setText(uSettings.getThreeLayerTemp()+ " F");
        flashMode.setText(uSettings.getFlashMode());

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                System.out.println(s);
                if(s.equals("onelayertemp")){
                    System.out.println("change layer one");
                    //oneLayerTemp.setText(sharedPreferences.getString("onelayertemp","")+ "F");
                    fb_manager.updateOneLayerTemp(Integer.parseInt(sharedPreferences.getString("onelayertemp","")));
                } else if (s.equals("threelayertemp")){
                    System.out.println("change on layer three");
                    //threeLayerTemp.setText(sharedPreferences.getString("threelayertemp", "")+ "F");
                    fb_manager.updateThreeLayerTemp(Integer.parseInt(sharedPreferences.getString("threelayertemp","")));
                } else if (s.equals("flashMode")){
                    System.out.println("change on layer three");
                    fb_manager.updateFlashMode(sharedPreferences.getString("flashMode",""));
                }
            }
        });
    }
}
