package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cs501.project.Model.Clothes;

import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Outfit;
import com.cs501.project.Model.RandomString;
import com.cs501.project.Model.Wardrobe;

import com.cs501.project.Model.Color;
import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Shirt;
import com.cs501.project.Model.Shoes;
import com.cs501.project.Model.User_settings;

import com.cs501.project.Model.Weather;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Random;



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

    private LinearLayout outfitLayout;

    private FireBaseManager fb_manager;

    private final static String TAG = "GenerateOutfitActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_outfit);

        temp = findViewById(R.id.tempratureSettings);
        description = findViewById(R.id.description);
        clouds = findViewById(R.id.clouds);
        wind = findViewById(R.id.wind);
        weatherTile = findViewById(R.id.weatherTitle);
        outfitLayout = findViewById(R.id.outfit_layout);

        fb_manager = FireBaseManager.getInstance();

        fb_manager = FireBaseManager.getInstance();



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
//                Outfit new_outfit = random_outfit();
                Outfit new_outfit = generateOutfitMonochrome();
                displayOutfit(new_outfit);
            }
        });

        Button random_outfit_button = (Button) findViewById(R.id.random_outfit_button);
        random_outfit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Outfit new_outfit = random_outfit();
                displayOutfit(new_outfit);
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

    public Outfit random_outfit(){

        // generate random number
        Random rand = new Random();

        // generate random outfit
        Wardrobe wardrobe = fb_manager.getUser().getWardrobe();

        // get clothes
        ArrayList<Clothes> top = wardrobe.getTShirts();
        ArrayList<Clothes> mid = wardrobe.getShirt();
        ArrayList<Clothes> bottom = wardrobe.getShorts();

        int top_arr_size = top.size(), mid_arr_size = mid.size(), bottom_arr_size = bottom.size();

        // generate the outfit
        Outfit new_outfit = new Outfit();
        String uniqueId = RandomString.getAlphaNumericString(16);
        new_outfit.setOutfitUniqueId(uniqueId);
        new_outfit.setName(uniqueId);

        if(top_arr_size > 0){
            // Generate random integers in range 0 to 999
            int rand_int1 = rand.nextInt(1000);
            new_outfit.addClothesToOutfit(top.get(rand_int1 % top_arr_size).getUniqueId());
        }
        if(mid_arr_size > 0){
            // Generate random integers in range 0 to 999
            int rand_int2 = rand.nextInt(1000);
            new_outfit.addClothesToOutfit(mid.get(rand_int2 % mid_arr_size).getUniqueId());
        }
        if(bottom_arr_size > 0){
            // Generate random integers in range 0 to 999
            int rand_int3 = rand.nextInt(1000);
            new_outfit.addClothesToOutfit(bottom.get(rand_int3 % bottom_arr_size).getUniqueId());
        }

        // save to database
        fb_manager.addOutfit(new_outfit);

        return new_outfit;
    }

    public void displayOutfit(Outfit outfit) {
        outfitLayout.removeAllViews();
        outfitLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.outfit_view_border));
        ArrayList<String> ids = outfit.getOutfit();
        for(String id: ids) {
            ImageView img = new ImageView(this);
            img.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            StorageReference pathReference = FirebaseStorage.getInstance().getReference();
            pathReference.child(fb_manager.getUser().getWardrobe().getClothesByUid(id).getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    try{
                        byte[] bytes = task.getResult();
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        img.setImageBitmap(b);
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Unable to parse image for the clothing. Please try again",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            outfitLayout.addView(img);
        }
    }

    public Outfit generateOutfitMonochrome (){

        User_settings uSettings = fb_manager.getUser().getUserSettings();

        ArrayList<Clothes> wardrobe = fb_manager.getClothes();

        int oneLayerTemp = uSettings.getOneLayerTemp();
        int threeLayerTemp = uSettings.getThreeLayerTemp();
        boolean topLayerWtrProof =false;

        int layers = determineOutfitLayers(oneLayerTemp, threeLayerTemp);

        if(weather.getWeatherType() == "Rain"){
            if (layers == 1){
                layers =2;
                topLayerWtrProof = true;
            }
            else{
                topLayerWtrProof = true;
            }
        }

        ArrayList<Clothes> top = new ArrayList<Clothes>();
        ArrayList<Clothes.Type> noDup = new ArrayList<Clothes.Type>();
        Clothes bottom = null;
        Clothes shoes=null;
        Color baseCol =null;

        for (int i= layers-1; i >= 0; i--){
            if (i == layers -1){ // select the first top item
                if (topLayerWtrProof == true){ // find a water resistant item
                    // no need to check duplicates, this is the first item being added
                    // REMEMBER: only add types that fit into top

                    for (int j = 0; j < wardrobe.size(); j++){
                        if (validItemsForLayer(i+1).contains(wardrobe.get(j).getType()) && wardrobe.get(j).isWaterResistant() == true ){ //if the current item is valid for this layer AND is waterproof
                            top.add(wardrobe.get(j)); // add item to top
                            noDup.add(wardrobe.get(j).getType());; //add item to no duplicates list
                            baseCol = wardrobe.get(j).getColor();
                            break;
                        }
                    }
                }

                else{ // find a non water resistant item
                    // no need to check duplicates, this is the first item being added
                    // REMEMBER: only add types that fit into top

                    for (int j = 0; j < wardrobe.size(); j++){
                        if (validItemsForLayer(i+1).contains(wardrobe.get(j).getType()) ){ //if the current item is valid for this layer
                            top.add(wardrobe.get(j)); // add item to top
                            noDup.add(wardrobe.get(j).getType());; //add item to no duplicates list
                            baseCol = wardrobe.get(j).getColor();
                            break;
                        }
                    }
                }

                // top layer chosen, now we choose bottom:
                // only add items that are valid for the BOTTOM slot  (pants or shorts)
                // REMEMBER: only add types that fit into bottom
                if (layers == 1 ){
                    for (Clothes item: wardrobe){
                        if ( (item.getType() == Clothes.Type.pants || item.getType() == Clothes.Type.shorts) && colorMatch(baseCol, item.getColor())){ //if the layer is 1, use shorts or pants that color match
                            bottom = item;
                            Log.d(TAG, "Hello world");
                            break;
                        }
                    }
                }
                else{
                    for (Clothes item: wardrobe){
                        if ( item.getType() == Clothes.Type.pants  && colorMatch(baseCol, item.getColor())){ //if the layer is NOT 1, use only pants that color match
                            bottom = item;
                            break;
                        }
                    }

                }
            }
            else{// We are no longer working on the top layer, and have also therefore chosen our bottoms

                //top layer of top and bottom chosen, choose remaining top layers
                for (int j = 0; j < wardrobe.size(); j++){
                    //check if item type has already been added, only new types allowed
                    //only add items that are valid for the TOP slot
                    //add item to no duplicates list
                    if ( validItemsForLayer(i+1).contains(wardrobe.get(j).getType())  && !noDup.contains(wardrobe.get(j).getType()) && colorMatch(baseCol, wardrobe.get(j).getColor())){ // if the current item's type has not been added yet, and it color matches, and its type is valid for TOP, add it
                        top.add(wardrobe.get(j)); // add the item
                        break;
                    }
                }
            }
        }

        // all top and bottom layers added, bottom chosen, now we choose shoes
        for (int j = 0; j < wardrobe.size(); j++){
            //check the item is shoes and color match
            if ( wardrobe.get(j).getType() == Clothes.Type.shoes && colorMatch(baseCol, wardrobe.get(j).getColor())){ // if the current item's type has not been added yet, and it color matches, add it
                shoes = wardrobe.get(j);
                break;
            }
        }

        Outfit outfit = new Outfit();

        for (Clothes item: top){
            outfit.addClothesToOutfit(item.getUniqueId());
        }

        if(bottom != null){
            outfit.addClothesToOutfit(bottom.getUniqueId());
        }

        if(shoes != null){
            outfit.addClothesToOutfit(shoes.getUniqueId());
        }

        return outfit;
    }

    public ArrayList<Clothes.Type>validItemsForLayer (int layer){
        ArrayList<Clothes.Type> arr = new ArrayList<>() ;

        if(layer == 1){
            arr.add(Clothes.Type.t_shirt);
            arr.add(Clothes.Type.shirt);
        }
        else if (layer ==2){
            arr.add(Clothes.Type.shirt);
            arr.add(Clothes.Type.jacket);
        }
        else{
            arr.add(Clothes.Type.jacket);
        }

        return arr;
    }


    public int determineOutfitLayers(int oneLyrTmp, int threeLyrTmp) {
        int layers = 0;
        if (weather.getCurrentTemp() > oneLyrTmp){
            layers = 1;
        }
        else if ( weather.getCurrentTemp() <= oneLyrTmp && weather.getCurrentTemp() >= threeLyrTmp){
            layers = 2;
        }
        else{
            layers= 3;
        }

        return layers;
    }

    public boolean colorMatch (@NonNull Color baseCol, @NonNull Color c2){

        float hueDiff = Math.abs(baseCol.getHsl1().get(0) - c2.getHsl1().get(0));

        if (colorDistance(baseCol, c2) < 400 && hueDiff < 10){ //if the colors are similar (within monochrome shaded range) OR if they share the same hue (if they are diff shades of the same color)
            return true;
        }
        else if ( c2.getHsl1().get(1) < 10 && c2.getHsl1().get(2) < 50  ){ // if the item is a shade of grey
            return true;
        }
        else if ((c2.getHsl1().get(2) > 90 && c2.getHsl1().get(1) < 15 ) || c2.getHsl1().get(2) < 7 ){  //if the item is black (luminance < 7) or white (lum > 90 AND sat < 15)
            return true;
        }
        return false;
    }

    // usefull for detemining if two colors are similar, works for colors of similar shades, even across different hues
    // (if you want to compare different shades of the same color, convert RGB to HSL, and look for colors of similar hue)
    public double colorDistance(Color e1, Color e2)
    {
        long rmean = ( (long)e1.getRed() + (long)e2.getRed() ) / 2;
        long r = (long)e1.getRed() - (long)e2.getRed();
        long g = (long)e1.getGreen() - (long)e2.getGreen();
        long b = (long)e1.getBlue() - (long)e2.getBlue();
        return java.lang.Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }
}