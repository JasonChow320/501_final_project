package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class ConfirmToWardrobe extends AppCompatActivity {

//    public static ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        System.out.println(fileNames.size() + " submitted");

        Bitmap b = BitmapFactory.decodeFile(fileNames.get(0));
        editItemImage.setImageBitmap(b);
    }
}