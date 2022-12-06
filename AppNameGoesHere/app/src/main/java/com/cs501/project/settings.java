package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
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
    int currOneTemp, currThreeTemp, currTheme;
    String CurrFlashMode;
    Spinner oneLayerTempSpinner, threeLayerTempSpinner, flashModeSpinner, themeSpinner;
    String[] flashModeList, oneLayerTempList, threeLayerTempList;
    ArrayAdapter adapter1, adapter2, adapter3 ,adapter4;
    ConstraintLayout con;
    User_settings uSettings;
    Switch themeSwitcher;

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
        uSettings = fb_manager.getUser().getUserSettings();

        Button back = (Button) findViewById(R.id.main_back_button);

        CurrFlashMode = uSettings.getFlashMode();
        currThreeTemp = uSettings.getThreeLayerTemp();
        currOneTemp = uSettings.getOneLayerTemp();
        currTheme = uSettings.getTheme();
        themeSwitcher = (Switch) findViewById(R.id.switch1);

        System.out.println(CurrFlashMode  + " " + currOneTemp + " " + currThreeTemp + " " + currTheme);

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

//        themeSpinner = (Spinner) findViewById(R.id.spinner6);
//        adapter4 = ArrayAdapter.createFromResource(this, R.array.themesNames, android.R.layout.simple_spinner_item);
//        themeSpinner.setAdapter(adapter4);

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

        con = findViewById(R.id.background);
        String backgroundColor = getResources().getStringArray(R.array.themesValues)[currTheme];
        con.setBackgroundColor(Color.parseColor(backgroundColor));
        oneLayerTempSpinner.setSelection(adapter1.getPosition(String.valueOf(currOneTemp)));
        threeLayerTempSpinner.setSelection(adapter2.getPosition(String.valueOf(currThreeTemp)));
        flashModeSpinner.setSelection(adapter3.getPosition(CurrFlashMode));
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        if(uSettings.getTheme() == 1) themeSwitcher.setChecked(true);
        else themeSwitcher.setChecked(false);



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

//        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                fb_manager.updateTheme(i);
//                String backgroundColor = getResources().getStringArray(R.array.themesValues)[i];
//                System.out.println(backgroundColor);
//                con.setBackgroundColor(Color.parseColor(backgroundColor));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        themeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    fb_manager.updateTheme(1);
                    String backgroundColor = getResources().getStringArray(R.array.themesValues)[1];
                    System.out.println(backgroundColor);
                    con.setBackgroundColor(Color.parseColor(backgroundColor));
                } else {
                    fb_manager.updateTheme(0);
                    String backgroundColor = getResources().getStringArray(R.array.themesValues)[0];
                    System.out.println(backgroundColor);
                    con.setBackgroundColor(Color.parseColor(backgroundColor));
                }

            }
        });

    }
}
