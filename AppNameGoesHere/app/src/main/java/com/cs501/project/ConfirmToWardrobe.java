package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.FireBaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.cs501.project.Model.Clothes;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmToWardrobe extends AppCompatActivity {

//    public static ArrayList<String> images = new ArrayList<>();
    RadioGroup clothingTypes;
    boolean imageReady = false;
    String[] color;

    private void rmBackground(String fileName) {
        //IMAGE STUFF
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    String filepath = Environment.getExternalStorageDirectory().toString() + "/images/test.png";
                    System.out.println(filepath);
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("image_file",fileName,
                                    RequestBody.create(MediaType.parse("image/png"),
                                            new File(fileName)))
                            .addFormDataPart("size","auto")
                            .build();
                    Request request = new Request.Builder()
                            .url("https://api.remove.bg/v1.0/removebg")
                            .method("POST", body)
                            .addHeader("X-Api-Key", "XdD9sWidE3fb2noUPs1CHYQA")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();

                        byte[] b = response.body().bytes();
                        Bitmap b1 = BitmapFactory.decodeByteArray(b, 0, b.length);
                        saveBitmap(b1, fileName);
                        System.out.println("RESPONSE SUCCESS");
                        imageReady = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("ERROR MAKING REQUEST " + e);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR REMOVING BACKGROUND " + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        Bitmap b = BitmapFactory.decodeFile(fileNames.get(0));
        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        editItemImage.setImageBitmap(b);
    }

    private String[] extractColor (String filename){



        Thread thread = new Thread(new Runnable() {



            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("media",filename,
                                    RequestBody.create(MediaType.parse("application/octet-stream"),
                                            new File(filename)))
                            .build();
                    Request request = new Request.Builder()
                            .url("https://api.sightengine.com/1.0/check.json?models=properties&api_user=596819012&api_secret=dy3YJZP5WJ3qtoYWqaXs")
                            .method("POST", body)
                            .build();
                    String r, g, b;

                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject obj = new JSONObject(response.body().string());
                         r = obj.getJSONObject("colors").getJSONObject("dominant").getString("r");
                         g = obj.getJSONObject("colors").getJSONObject("dominant").getString("g");
                         b = obj.getJSONObject("colors").getJSONObject("dominant").getString("b");

                         System.out.println("COLOR IS: "+ r + " " + g + " " +  b);

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("ERROR MAKING REQUEST " + e);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR EXTRACTING COLOR " + e);
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;

    }

    private final String TAG = "ConfirmToWardrobe";

    private FireBaseManager fb_manager;
    private Clothes_Factory clothes_factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "Entering onCreate for ConfirmToWardrobe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);
        
        clothes_factory = new Clothes_Factory();
        fb_manager = FireBaseManager.getInstance();

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        Button confirm = (Button) findViewById(R.id.ConfirmAdd);
        clothingTypes = (RadioGroup) findViewById(R.id.clothingTypes);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        System.out.println(fileNames.size() + " submitted");

        rmBackground(fileNames.get(0));
        extractColor(fileNames.get(0));



//        Bitmap b = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/images/test.png");
//        while (!imageReady) {
//
//        }
//        Bitmap b = BitmapFactory.decodeFile(fileNames.get(0));
//        editItemImage.setImageBitmap(b);
            //        System.out.println(b.getHeight() + " x " + b.getWidth());

        String[] types = Clothes.getTypes(Clothes.Type.class);
        for (int i = 0; i < types.length; i++) {
            RadioButton current = new RadioButton(this);
            current.setText(types[i]);
            clothingTypes.addView(current, i);
        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //1. Add submission to database TODO

                // get selected radio button from radioGroup
                int selectedId = clothingTypes.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radio_button = (RadioButton) findViewById(selectedId);
                Log.d(TAG, (String) radio_button.getText());

                Clothes new_clothes = getClothes(String.valueOf(radio_button.getText()));

                if(new_clothes == null){
                    // TODO ERROR! do something
                    return;
                }

                // Add to database
                fb_manager.addClothes(new_clothes);

                //2. Remove 1st item of arraylist and refresh activity with new list
                //3. At end of list return to menu.
                //Question: How can we reuse this Activity for editing clothes items?

                fileNames.remove(0);
                if (fileNames.size() > 0) {
                    Intent i = new Intent(ConfirmToWardrobe.this, ConfirmToWardrobe.class);
                    i.putExtra("fileNames", fileNames);
                    finish();
                    startActivity(i);
                } else {
                    finish();
                }
            }
        });
    }

    private Clothes getClothes(String type){
        try{
            Clothes.Type clothes_type = Clothes.Type.valueOf(type);
            switch(clothes_type){
                case jacket:
                    return clothes_factory.get_jacket();
                case t_shirt:
                    return clothes_factory.get_tshirt();
                case shirt:
                    return clothes_factory.get_shirt();
                case shoes:
                    return clothes_factory.get_shoes();
                case pants:
                    return clothes_factory.get_pants();
                case shorts:
                    return clothes_factory.get_shorts();
                default:
                    // should never get to this point unless we add now types
                    return clothes_factory.get_shirt();
            }
        } catch (IllegalArgumentException e){
            // shouldn't happen since we create the radio button text using the enum list
            Log.e(TAG, "Unable to convert stirng to clothes types");
        }

        return null;
    }
    
    //https://stackoverflow.com/questions/63410194/how-to-save-multiple-bitmaps-fastly-in-android-studio
    public void saveBitmap(Bitmap output, String fileName){
     
        System.out.println("Saved to " + fileName);
        File image = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(image);
            output.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("ERROR SAVING IMAGE: " + e);
        }
    }
}