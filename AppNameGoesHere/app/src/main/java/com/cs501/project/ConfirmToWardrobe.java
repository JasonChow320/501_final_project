package com.cs501.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.EnumSet;

public class ConfirmToWardrobe extends AppCompatActivity {

//    public static ArrayList<String> images = new ArrayList<>();

    private final String TAG = "ConfirmToWardrobe";

    private FireBaseManager fb_manager;
    private Clothes_Factory clothes_factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "Entering onCreate for ConfirmToWardrobe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        Button confirm = (Button) findViewById(R.id.ConfirmAdd);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        System.out.println(fileNames.size() + " submitted");

        Bitmap b = BitmapFactory.decodeFile(fileNames.get(0));
        editItemImage.setImageBitmap(b);

        // TODO Bitmap was getting errors, so I commented this out
        //System.out.println(b.getHeight() + " x " + b.getWidth());

        // set up radio buttons with corresponding clothing types
        RadioGroup radio_group = (RadioGroup) findViewById(R.id.clothes_category);
        int count = radio_group.getChildCount();
        ArrayList<Clothes.Type> clothes_type = new ArrayList<Clothes.Type>(EnumSet.allOf(Clothes.Type.class));

        for (int i=0;i<count;i++) {

            RadioButton button = (RadioButton) radio_group.getChildAt(i);
            if (button instanceof RadioButton) {
                button.setText(clothes_type.get(i).name());
            }
        }

        clothes_factory = new Clothes_Factory();
        fb_manager = FireBaseManager.getInstance();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1. Add submission to database TODO

                // get selected radio button from radioGroup
                int selectedId = radio_group.getCheckedRadioButtonId();

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
}