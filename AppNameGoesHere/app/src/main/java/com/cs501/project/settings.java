package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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
    int currOneTemp, currThreeTemp;
    String CurrFlashMode;
    Spinner oneLayerTempSpinner, threeLayerTempSpinner, flashModeSpinner;
    String[] flashModeList, oneLayerTempList, threeLayerTempList;
    ArrayAdapter adapter1, adapter2, adapter3;

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

        Button back = (Button) findViewById(R.id.main_back_button);

        CurrFlashMode = uSettings.getFlashMode();
        currThreeTemp = uSettings.getThreeLayerTemp();
        currOneTemp = uSettings.getOneLayerTemp();

        System.out.println(CurrFlashMode  + " " + currOneTemp + " " + currThreeTemp);

        oneLayerTempList = getResources().getStringArray(R.array.tempValues1Layer);
        flashModeList = getResources().getStringArray(R.array.flashModes);
        threeLayerTempList = getResources().getStringArray(R.array.tempValues3Layers);

        oneLayerTemp = (TextView) findViewById(R.id.textView6);
        threeLayerTemp = (TextView) findViewById(R.id.textView7);
        flashMode = (TextView) findViewById(R.id.textView4);

        oneLayerTempSpinner = (Spinner) findViewById(R.id.spinner5);
        adapter1 = ArrayAdapter.createFromResource(this, R.array.tempValues1Layer ,android.R.layout.simple_spinner_item);
        oneLayerTempSpinner.setAdapter(adapter1);

        threeLayerTempSpinner = (Spinner) findViewById(R.id.spinner4);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.tempValues3Layers ,android.R.layout.simple_spinner_item);
        threeLayerTempSpinner.setAdapter(adapter2);
        threeLayerTempSpinner.setSelection(adapter2.getPosition(currThreeTemp));

        flashModeSpinner = (Spinner) findViewById(R.id.spinner3);
        adapter3 = ArrayAdapter.createFromResource(this, R.array.flashModes ,android.R.layout.simple_spinner_item);
        flashModeSpinner.setAdapter(adapter3);
        System.out.println(adapter3.getPosition(CurrFlashMode));
        flashModeSpinner.setSelection(adapter3.getPosition(CurrFlashMode));

        // Settings auto updates when database changes
        myRef.child(currentUser.getUid()).child("users").child(String.valueOf(fb_manager.getUserIdx())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                User user = dataSnapshot.getValue(User.class);

                User_settings setting = user.getUserSettings();

            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        oneLayerTempSpinner.setSelection(adapter1.getPosition(String.valueOf(currOneTemp)));
        threeLayerTempSpinner.setSelection(adapter2.getPosition(String.valueOf(currThreeTemp)));
        flashModeSpinner.setSelection(adapter3.getPosition(CurrFlashMode));


        oneLayerTempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = oneLayerTempList[i];
                fb_manager.updateOneLayerTemp(Integer.parseInt(temp));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        threeLayerTempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = threeLayerTempList[i];
                fb_manager.updateThreeLayerTemp(Integer.parseInt(temp));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        flashModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = flashModeList[i];
                fb_manager.updateFlashMode(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
