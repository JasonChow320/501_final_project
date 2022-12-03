package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs501.project.Model.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GenerateOutfit extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private double latitude;
    private double longitude;
    private Weather weather;
    private TextView temp;
    private TextView description;
    private TextView clouds;
    private TextView wind;
    private TextView weatherTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_outfit);

        temp = findViewById(R.id.tempratureSettings);
        description = findViewById(R.id.description);
        clouds = findViewById(R.id.clouds);
        wind = findViewById(R.id.wind);
        weatherTile = findViewById(R.id.weatherTitle);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLocation();
        }

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLoc = result.get("android.permission.ACCESS_FINE_LOCATION");
            boolean coraseLoc = result.get("android.permission.ACCESS_COARSE_LOCATION");
            System.out.println(fineLoc + " " + coraseLoc);
            if (!fineLoc || !coraseLoc) {
                Toast toast = Toast.makeText(GenerateOutfit.this, "App does not have location permissions from device", Toast.LENGTH_LONG);
                toast.show();
            } else {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                getLocation();
            }
        });

        locationPermissionRequest.launch(new String[]{ //request location permissions
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION});

        // Generate random outfit (for now)
        Button generate_outfit = (Button) findViewById(R.id.generate_outfit_button);
        generate_outfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                random_outfit();
            }
        });
    }

    //uses latitude and longitude to get current weather
    public void getWeather(String lat, String lon) {

        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=08473323d957a10a040eb70deb579c9e";
        mRequestQueue = Volley.newRequestQueue(this);

        // String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String info = null;
                String weatherType = null;
                String weatherDescription = null;
                double windSpeed = 0;
                double windGusts = 0;
                double clouds = 0;
                double humidity = 0;
                System.out.println(obj);
                try {
                    info = obj.getJSONObject("main").getString("temp");
                    JSONObject weatherINfo = obj.getJSONArray("weather").getJSONObject(0);
                    weatherType = weatherINfo.getString("main");
                    weatherDescription = weatherINfo.getString("description");
                    windSpeed = Math.floor(Double.valueOf(obj.getJSONObject("wind").getString("speed")) * 2.237);
                    windGusts = Math.floor(Double.valueOf(obj.getJSONObject("wind").getString("gust")) * 2.237);
                    clouds = Double.valueOf(obj.getJSONObject("clouds").getString("all"));
                    humidity = Double.valueOf(obj.getJSONObject("main").getString("humidity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                double temp = Math.floor((Double.valueOf(info) - 273.15) * 9 / 5 + 32);
                weather = new Weather(temp, weatherType, weatherDescription, windSpeed, windGusts, clouds, humidity);
                populateWeatherLayout(weather);
                System.out.println(weather.getCurrentTemp());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });

        mRequestQueue.add(mStringRequest);

    }


    public void getLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation() //permissions checked and asked on onCreate
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            getWeather(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                });
    }

    public void populateWeatherLayout(Weather weather){ //generates layout of current weather description
        weatherTile.setText("Current Weather");
        weatherTile.setText("Current Weather");
        description.setText("Description: " + weather.getWeatherDes());
        temp.setText("Temperature: " + String.valueOf(weather.getCurrentTemp()) + " F");
        clouds.setText("Cloud cover: "+String.valueOf(weather.getClouds()) + "%");
        wind.setText("Wind speed: " + String.valueOf(weather.getWindSpeed()) +" MPH");
    }


    public void random_outfit(){

    }
    
    public void generateOutfitMonochrome (){

    }
}