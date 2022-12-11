package com.cs501.project;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.cs501.project.Model.Hash;
import com.cs501.project.Model.Outfit;
import com.cs501.project.Model.RandomString;
import com.cs501.project.Model.User;
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

import java.util.Collections;
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
    private Outfit currentFit = null;

    private LinearLayout outfitLayout;

    private FireBaseManager fb_manager;

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLocation();
        }

        ActivityResultLauncher<String[]> locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLoc = result.get("android.permission.ACCESS_FINE_LOCATION");
            boolean coraseLoc = result.get("android.permission.ACCESS_COARSE_LOCATION");
            System.out.println(fineLoc + " " + coraseLoc);
            if (!fineLoc || !coraseLoc) {
                Toast toast = Toast.makeText(GenerateOutfit.this, getResources().getString(R.string.loc_permissions), Toast.LENGTH_LONG);
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
                if(new_outfit!=null) {
                    displayOutfit(new_outfit);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_monochrome),
                            Toast.LENGTH_SHORT).show();
                }
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

        Button save_outfit = (Button) findViewById(R.id.save_outfit);
        save_outfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFit!=null) {

                    // ask for outfit name
                    AlertDialog.Builder pass_builder = new AlertDialog.Builder(GenerateOutfit.this);
                    pass_builder.setCancelable(true);
                    pass_builder.setTitle(getResources().getString(R.string.outfit_name));
                    pass_builder.setMessage(getResources().getString(R.string.name_outfit_question));
                    final EditText input = new EditText(GenerateOutfit.this);
                    input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(13) });
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    pass_builder.setView(input);
                    pass_builder.setPositiveButton(getResources().getString(R.string.confirm),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(input.getText().toString() != null && input.getText().toString().length() > 0){
                                        currentFit.setName(input.getText().toString());
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.outfit_saved),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        currentFit.setName("Outfit");
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_no_name),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    fb_manager.addOutfit(currentFit);
                                }
                            });
                    pass_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            currentFit.setName("Outfit");
                            fb_manager.addOutfit(currentFit);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.outfit_saved),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    AlertDialog pass_dialog = pass_builder.create();
                    pass_dialog.show();
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        ConstraintLayout con = findViewById(R.id.background);
        String backgroundColor = getResources().getStringArray(R.array.themesValues)[uSettings.getTheme()];
        System.out.println(backgroundColor);
        con.setBackgroundColor(android.graphics.Color.parseColor(backgroundColor));
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
        weatherTile.setText(getResources().getString(R.string.current_weather));
        description.setText(getResources().getString(R.string.description) + weather.getWeatherDes());
        temp.setText(getResources().getString(R.string.tempLabel) + String.valueOf(weather.getCurrentTemp()) + " F");
        clouds.setText(getResources().getString(R.string.cloud_label)+String.valueOf(weather.getClouds()) + "%");
        wind.setText(getResources().getString(R.string.windLabel) + String.valueOf(weather.getWindSpeed()) +" MPH");
    }

    public Outfit random_outfit(){

        // generate random number
        Random rand = new Random();

        // generate random outfit
        Wardrobe wardrobe = fb_manager.getUser().getWardrobe();

        // get clothes
        ArrayList<Clothes> top = wardrobe.getLightJackets();
        top.addAll(wardrobe.getHeavyJackets());
        top.addAll(wardrobe.getSweater());
        ArrayList<Clothes> mid = wardrobe.getLongSleeve();
        mid.addAll(wardrobe.getTShirts());
        ArrayList<Clothes> bottom = wardrobe.getShorts();
        bottom.addAll(wardrobe.getPants());
        ArrayList<Clothes> shoes = wardrobe.getShoes();

        int top_arr_size = top.size(), mid_arr_size = mid.size(), bottom_arr_size = bottom.size(), shoe_arr_size=shoes.size();

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
        if(shoe_arr_size > 0){
            // Generate random integers in range 0 to 999
            int rand_int4 = rand.nextInt(1000);
            new_outfit.addClothesToOutfit(shoes.get(rand_int4 % shoe_arr_size).getUniqueId());
        }

        // save to database
        //fb_manager.addOutfit(new_outfit);

        return new_outfit;
    }

    public void displayOutfit(Outfit outfit) {
        currentFit = outfit;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        System.out.println(height);

        outfitLayout.removeAllViews();
        outfitLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.outfit_view_border));
        ArrayList<String> ids = outfit.getOutfit();
        int number = ids.size();

        Wardrobe wardrobe = fb_manager.getUser().getWardrobe();

        for(String id: ids) {
            ImageView img = new ImageView(this);
            img.setLayoutParams(new LinearLayout.LayoutParams(width/2, (height/(number+1))));

            Clothes clothes = wardrobe.getClothesByUid(id);

            // get image
            User user = fb_manager.getUser();
            boolean displayImg = false, cache = (user.getUserSettings().getEnableCache() == 1);

            if(cache) {

                System.out.println("Trying to decode: " + fb_manager.getImagePath() + "/" + clothes.getImageURL());

                Bitmap b = BitmapFactory.decodeFile(fb_manager.getImagePath() + "/" + clothes.getImageURL());
                if (b == null) {
                    System.out.println("Can't decode image");
                } else {
                    img.setImageBitmap(b);
                    System.out.println("Displaying image from cache");
                    displayImg = true;
                }
            }

            if(!displayImg) {

                // retrieve image from database
                StorageReference pathReference = FirebaseStorage.getInstance().getReference();
                pathReference.child(clothes.getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        try {
                            byte[] bytes = task.getResult();
                            Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            img.setImageBitmap(b);

                            if (cache) {
                                fb_manager.saveBitmap(b, fb_manager.getImagePath() + "/" + clothes.getImageURL());
                                System.out.println("Adding to cache for cache enabled user");
                            }
                        } catch (Exception e) {
                            Toast.makeText(GenerateOutfit.this, getResources().getString(R.string.unable_retrieve_clothes), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            outfitLayout.addView(img);
        }
    }

    public Outfit generateOutfitMonochrome (){

        User_settings uSettings = fb_manager.getUser().getUserSettings();
        ArrayList<Clothes> wardrobeOrig = fb_manager.getClothes();

        ArrayList<Clothes> wardrobe = new ArrayList<>(wardrobeOrig);
        Collections.shuffle(wardrobe);

        int oneLayerTemp = uSettings.getOneLayerTemp();
        int threeLayerTemp = uSettings.getThreeLayerTemp();
        boolean topLayerWtrProof =false;

        int layers = determineOutfitLayers(oneLayerTemp, threeLayerTemp);

        if(weather.getWeatherType().contains("Rain") || weather.getWeatherType().contains("rain") || weather.getWeatherType().contains("snow") || weather.getWeatherType().contains("Snow") ){
            if (layers == 1){
                layers = 2;
                topLayerWtrProof = true;
            }
            else{
                topLayerWtrProof = true;
            }
        }

        ArrayList<Clothes> top = new ArrayList<Clothes>();
        ArrayList<Clothes.Type> noDup = new ArrayList<Clothes.Type>();
        Clothes bottom = null;
        Clothes shoes = null;
        Color baseCol =null;

        for (int i = layers-1; i >= 0; i--){
            if (i == layers -1){ // select the first top item
                if (topLayerWtrProof == true){ // find a water resistant item
                    // no need to check duplicates, this is the first item being added
                    // REMEMBER: only add types that fit into top

                    for (int j = 0; j < wardrobe.size(); j++){
                        if (validItemsForLayer(i+1).contains(wardrobe.get(j).getType()) && wardrobe.get(j).isWaterResistant() == true ){ //if the current item is valid for this layer AND is waterproof
                            top.add(wardrobe.get(j)); // add item to top
                            noDup.add(wardrobe.get(j).getType());; //add item to no duplicates list

                            if (checkIfBlackWhite(wardrobe.get(j).getColor()) == false){
                                baseCol = wardrobe.get(j).getColor();
                            }

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

                            if (checkIfBlackWhite(wardrobe.get(j).getColor()) == false){
                                baseCol = wardrobe.get(j).getColor();
                            }

                            break;
                        }
                    }
                }

                // top layer chosen, now we choose bottom:
                // only add items that are valid for the BOTTOM slot  (pants or shorts)
                // REMEMBER: only add types that fit into bottom
                if (layers == 1 ){
                    for (Clothes item: wardrobe){
                        if ( (item.getType() == Clothes.Type.PANTS || item.getType() == Clothes.Type.SHORTS)){ //if the layer is 1, use shorts or pants
                            if (baseCol != null &&  colorMatch(baseCol, item.getColor()) ){ // weve already selected a base color, we just neet to see if the item matches
                                bottom = item;
                                break;
                            }
                            else if (baseCol == null && checkIfBlackWhite(item.getColor())){ //we don't yet have a base color, and this item is black or white, we can select it, but it cannot be used as our base color
                                bottom = item;
                                break;
                            }
                            else if (baseCol == null && checkIfBlackWhite(item.getColor()) == false ){ //we don't yet have a base color, and this item HAS color, so it must be used as our base color
                                bottom = item;
                                baseCol = item.getColor();
                                break;
                            }
                            //nothing found, keep looping

                        }
                    }
                }
                else{
                    for (Clothes item: wardrobe){
                        if ( item.getType() == Clothes.Type.PANTS ){ //if the layer is 1, use shorts or pants
                            if (baseCol != null &&  colorMatch(baseCol, item.getColor()) ){ // weve already selected a base color, we just neet to see if the item matches
                                bottom = item;
                                break;
                            }
                            else if (baseCol == null && checkIfBlackWhite(item.getColor())){ //we don't yet have a base color, and this item is black or white, we can select it, but it cannot be used as our base color
                                bottom = item;
                                break;
                            }
                            else if (baseCol == null && checkIfBlackWhite(item.getColor()) == false ){ //we don't yet have a base color, and this item HAS color, so it must be used as our base color
                                bottom = item;
                                baseCol = item.getColor();
                                break;
                            }
                            //nothing found, keep looping

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
                    if ( validItemsForLayer(i+1).contains(wardrobe.get(j).getType())  && !noDup.contains(wardrobe.get(j).getType())){ // if the current item's type has not been added yet, and its type is valid for TOP:


                        if (baseCol == null && checkIfBlackWhite(wardrobe.get(j).getColor()) == false){ //all items in outfit are currently back or white, this item has color, set it as baseCol
                            baseCol = wardrobe.get(j).getColor();
                            top.add(wardrobe.get(j));
                            break;

                        }
                        else if (baseCol == null && checkIfBlackWhite(wardrobe.get(j).getColor())){ //all items in outfit are currently back or white, this item does not have color, do not set it as baseCol
                            top.add(wardrobe.get(j));
                            break;
                        }
                        else if (baseCol != null && colorMatch(baseCol, wardrobe.get(j).getColor())){ //we already have a colorful item, we just neet to see if this item matches
                            top.add(wardrobe.get(j));
                            break;
                        }

                        //If weve already selected an item with color (baseCol != null), only items that dont match it will be ignored
                    }
                }
            }
        }

        // all top and bottom layers added, bottom chosen, now we choose shoes

        for (int j = 0; j < wardrobe.size(); j++){
            //check the item is shoes and color match
            if ( wardrobe.get(j).getType() == Clothes.Type.SHOES ){ // if the current item's type is shoes :


                if (baseCol == null && checkIfBlackWhite(wardrobe.get(j).getColor()) == false){
                    baseCol = wardrobe.get(j).getColor();
                    shoes = wardrobe.get(j);
                    break;
                }
                else if (baseCol == null && checkIfBlackWhite(wardrobe.get(j).getColor()) ){
                    shoes = wardrobe.get(j);
                    break;
                }
                else if (baseCol != null && colorMatch(baseCol, wardrobe.get(j).getColor()) || checkIfBlackWhite(wardrobe.get(j).getColor())){
                    shoes = wardrobe.get(j);
                    break;
                }
            }
        }

        if(top.size()!= layers || bottom==null || shoes == null) {
            return null; //outfit was not created successfully
        }

        Outfit outfit = new Outfit();
        String uniqueId = RandomString.getAlphaNumericString(16);
        outfit.setOutfitUniqueId(uniqueId);
        outfit.setName(uniqueId);

        for (Clothes item: top){
            outfit.addClothesToOutfit(item.getUniqueId());
        }

        outfit.addClothesToOutfit(bottom.getUniqueId());
        outfit.addClothesToOutfit(shoes.getUniqueId());

        return outfit;
    }

    public ArrayList<Clothes.Type>validItemsForLayer (int layer){
        ArrayList<Clothes.Type> arr = new ArrayList<>() ;


        if(layer == 1){
            arr.add(Clothes.Type.T_SHIRT);
            arr.add(Clothes.Type.LONG_SLEEVE);
        }
        else if (layer ==2){
            if (weather.getCurrentTemp() < 32){
                arr.add(Clothes.Type.SWEATER);
                arr.add(Clothes.Type.HEAVY_JACKET);
            }
            else{
                arr.add(Clothes.Type.SWEATER);
                arr.add(Clothes.Type.LIGHT_JACKET);
                arr.add(Clothes.Type.HEAVY_JACKET);
            }
        }
        else{
            if (weather.getCurrentTemp() < 32){
              
                arr.add(Clothes.Type.HEAVY_JACKET);
            }
            else{
                arr.add(Clothes.Type.LIGHT_JACKET);
                arr.add(Clothes.Type.HEAVY_JACKET);
            }
        }

        return arr;
    }


    public int determineOutfitLayers(int oneLyrTmp, int threeLyrTmp) {
        try{
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
        } catch (Error error) {
            return 2;
        }
    }

    public boolean colorMatch (@NonNull Color baseCol, @NonNull Color c2){

        if(baseCol == null || c2 == null){
            return false;
        }

        float hueDiff = Math.abs(baseCol.getHsl1().get(0) - c2.getHsl1().get(0));

        if (colorDistance(baseCol, c2) < 400 && hueDiff < 12){ //if the colors are similar (within monochrome shaded range) AND if they share the same hue (if they are diff shades of the same color)
            return true;
        }
        //if the color is not black or white or any shade of gray, then it is not monochrome

        return checkIfBlackWhite(c2);

    }

    public boolean checkIfBlackWhite (Color c2){

        if ((c2.getHsl1().get(2) > 87 && c2.getHsl1().get(1) < 9 ) || (c2.getHsl1().get(2) < 7) || (c2.getHsl1().get(2) < 23 && c2.getHsl1().get(1) <= 35 && (c2.getHsl1().get(0) > 200 && c2.getHsl1().get(0) < 235 ))  ){  //if the item is black (luminance < 7) or white (lum > 90 AND sat < 15)
            return true;
        }
        else if ( c2.getHsl1().get(1) < 8 ){ // if the item is a shade of grey (hues between 30 and 250 tend to show color so we must exclude them)
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
