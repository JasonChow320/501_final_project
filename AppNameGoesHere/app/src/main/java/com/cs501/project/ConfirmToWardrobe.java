package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cs501.project.Model.Clothes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        Button confirm = (Button) findViewById(R.id.ConfirmAdd);
        clothingTypes = (RadioGroup) findViewById(R.id.clothingTypes);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        System.out.println(fileNames.size() + " submitted");

        rmBackground(fileNames.get(0));

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
                    //1. Add submission to database
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

    //https://stackoverflow.com/questions/63410194/how-to-save-multiple-bitmaps-fastly-in-android-studio
    public void saveBitmap(Bitmap output, String fileName){
        String filepath = Environment.getExternalStorageDirectory().toString() + "/images";
//        File dir = new File(filepath);
//        if(!dir.exists()){
//            dir.mkdir();
//        }
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