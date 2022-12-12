package com.cs501.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.User;
import com.cs501.project.Model.User_settings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class settings extends AppCompatActivity {
    /**
     *
     */

    TextView oneLayerTemp;
    TextView threeLayerTemp;
    TextView flashMode;
    int currOneTemp, currThreeTemp, currTheme;
    String CurrFlashMode;
    Spinner oneLayerTempSpinner, threeLayerTempSpinner, flashModeSpinner;
    String[] flashModeList, oneLayerTempList, threeLayerTempList;
    ArrayAdapter adapter1, adapter2, adapter3;
    ConstraintLayout con;
    User_settings uSettings;
    Switch themeSwitcher, cacheSwitcher;
    ImageView themeIcon;

    ProgressBar progressBar_settings;

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

        Button backButton = (Button) findViewById(R.id.settings_back_button);
        Button toggle_language = (Button) findViewById(R.id.toggle_language);
        Button delete_cache = (Button) findViewById(R.id.delete_cache);

        CurrFlashMode = uSettings.getFlashMode();
        currThreeTemp = uSettings.getThreeLayerTemp();
        currOneTemp = uSettings.getOneLayerTemp();
        currTheme = uSettings.getTheme();
        themeSwitcher = (Switch) findViewById(R.id.switch1);
        cacheSwitcher = (Switch) findViewById(R.id.cache_switch);

        progressBar_settings = (ProgressBar) findViewById(R.id.progressBar_settings);
        progressBar_settings.setVisibility(View.GONE);

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

        int index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[0]);
        if(CurrFlashMode.equals("Off"))
            index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[1]);
        else if(CurrFlashMode.equals("Auto"))
            index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[2]);

        flashModeSpinner.setSelection(index);

        themeIcon = (ImageView) findViewById(R.id.themeIcon);

        // Settings auto updates when database changes
        myRef.child(currentUser.getUid()).child("users").child(String.valueOf(fb_manager.getUserIdx())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                User user = dataSnapshot.getValue(User.class);

                if(user != null) {
                    User_settings setting = user.getUserSettings();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

        delete_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb_manager = FireBaseManager.getInstance();

                // define confirm delete cache dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(settings.this);
                builder.setCancelable(true);
                builder.setTitle(getResources().getString(R.string.dialog_delete_cache));
                builder.setMessage(getResources().getString(R.string.dialog_delete_cache_msg));
                builder.setPositiveButton(getResources().getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // delete password
                                fb_manager.deleteCache();
                                Toast.makeText(settings.this, "Cache deleted",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog confirm_dialog = builder.create();
                confirm_dialog.show();
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
        int index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[0]);
        if(CurrFlashMode.equals("Off"))
            index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[1]);
        else if(CurrFlashMode.equals("Auto"))
            index = adapter3.getPosition(getResources().getStringArray(R.array.flashModes)[2]);

        flashModeSpinner.setSelection(index);
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        if(uSettings.getTheme() == 1) {
            themeSwitcher.setChecked(true);
            themeIcon.setImageDrawable(getResources().getDrawable(R.drawable.moon_6686));
        }
        else {
            themeSwitcher.setChecked(false);
            themeIcon.setImageDrawable(getResources().getDrawable(R.drawable.sun_8761));
        }

        if(uSettings.getEnableCache() == 1) {
            cacheSwitcher.setChecked(true);
        } else {
            cacheSwitcher.setChecked(false);
        }

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
                if(temp.equals("Encendido"))
                    temp = "On";
                else if (temp.equals("Apagado"))
                    temp = "Off";
                else if (temp.equals("Automático"))
                    temp = "Auto";
                fb_manager.updateFlashMode(temp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        themeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    fb_manager.updateTheme(1);
                    String backgroundColor = getResources().getStringArray(R.array.themesValues)[1];
                    System.out.println(backgroundColor);
                    con.setBackgroundColor(Color.parseColor(backgroundColor));
                    themeIcon.setImageDrawable(getResources().getDrawable(R.drawable.moon_6686));
                } else {
                    fb_manager.updateTheme(0);
                    String backgroundColor = getResources().getStringArray(R.array.themesValues)[0];
                    System.out.println(backgroundColor);
                    con.setBackgroundColor(Color.parseColor(backgroundColor));
                    themeIcon.setImageDrawable(getResources().getDrawable(R.drawable.sun_8761));

                }

            }
        });

        cacheSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    progressBar_settings.setVisibility(View.VISIBLE);
                    fb_manager.setEnableCache(1);
                    progressBar_settings.setVisibility(View.GONE);
                } else {
                    progressBar_settings.setVisibility(View.VISIBLE);
                    fb_manager.setEnableCache(0);
                    progressBar_settings.setVisibility(View.GONE);
                }
            }
        });
    }
}
