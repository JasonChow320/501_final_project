package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class settings extends AppCompatActivity {

    TextView oneLayerTemp;
    TextView twoLayerTemp;
    TextView threeLayerTemp;
    int currOneTemp;
    int currThreeTemp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, new settingsFragment())
                .commit();

        oneLayerTemp = (TextView) findViewById(R.id.textView6);
        threeLayerTemp = (TextView) findViewById(R.id.textView7);


        currOneTemp = 0;
        currThreeTemp = 0;
        int t = R.xml.settings;

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        oneLayerTemp.setText(sharedPreferences.getString("onelayertemp","")+ "F");
        threeLayerTemp.setText(sharedPreferences.getString("threelayertemp", "")+ "F");

        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                System.out.println(s);
                if(s.equals("onelayertemp")){
                    System.out.println("change layer one");
                    oneLayerTemp.setText(sharedPreferences.getString("onelayertemp","")+ "F");
                } else if (s.equals("threelayertemp")){
                    System.out.println("change on layer three");
                    threeLayerTemp.setText(sharedPreferences.getString("threelayertemp", "")+ "F");
                }
            }
        });

    }
}