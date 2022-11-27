package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cs501.project.Model.Clothes;

import java.util.ArrayList;

public class ConfirmToWardrobe extends AppCompatActivity {

//    public static ArrayList<String> images = new ArrayList<>();
    RadioGroup clothingTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        Button confirm = (Button) findViewById(R.id.ConfirmAdd);
        clothingTypes = (RadioGroup) findViewById(R.id.clothingTypes);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        System.out.println(fileNames.size() + " submitted");

        Bitmap b = BitmapFactory.decodeFile(fileNames.get(0));
        editItemImage.setImageBitmap(b);
//        System.out.println(b.getHeight() + " x " + b.getWidth());

        String[] types = Clothes.getTypes(Clothes.Type.class);
        for(int i = 0; i < types.length; i++){
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
                if(fileNames.size() > 0) {
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
}