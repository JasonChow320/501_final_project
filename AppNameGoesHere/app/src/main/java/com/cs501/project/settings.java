package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
//import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class settings extends AppCompatActivity {

    TextView oneLayerTemp;
    TextView twoLayerTemp;
    TextView threeLayerTemp;
    int currOneTemp;
    int currThreeTemp;
    Response image;
    Bitmap bitmap;

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

        Thread thread = new Thread(new Runnable() {
        boolean shouldContinue = true;
            @Override
            public void run() {
                while(shouldContinue){
                    try{
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        MediaType mediaType = MediaType.parse("text/plain");
                        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("image_url","https://images.squarespace-cdn.com/content/v1/61dcd32b3fb8bb4b5af9b560/2469e772-d4de-4dcd-ade0-49183fe7d087/american-made-clothing.jpg")
                                .addFormDataPart("size","auto")
                                .build();
                        Request request = new Request.Builder()
                                .url("https://api.remove.bg/v1.0/removebg")
                                .method("POST", body)
                                .addHeader("X-Api-Key", "b8t7Hp7Czt8F4V7dFFGwYdE2")
                                .build();
                        Response response = client.newCall(request).execute();
                        image = response;

                    } catch (IOException e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }
                    shouldContinue = false;

                }
            }

        });
        thread.start();

        while(image == null){

        }

        toimage(image);

    }

    public void toimage(Response response) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);

        Thread thread = new Thread(new Runnable() {
            boolean shouldContinue = true;
            @Override
            public void run() {

                ResponseBody in = response.body();
                InputStream inputStream = in.byteStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            }

        });
        thread.start();
        while(bitmap == null){

        }
        imageView.setImageBitmap(bitmap);
    }

}