package com.cs501.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.Clothes_Factory;
import com.cs501.project.Model.Color;
import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.User_settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    RadioGroup clothingTypes, waterproofSetting;
    boolean imageReady = false;
    Color color;
    View mainCol;
    View accCol;

    // Max size of the image
    public final static int MAX_IMAGE_SIZE = 1000000;

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
                            .addHeader("X-Api-Key", "wF6Vq9LxUUhmKRUACJPCTULb")
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

    private Color extractColor (String filename){
        final Color[] c = new Color[1];
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
                    float r, g, b, r2, g2, b2;
                    String hex1, hex2;

                    try {
                        Response response = client.newCall(request).execute();
                        JSONObject obj = new JSONObject(response.body().string());

                        hex1 = (obj.getJSONObject("colors").getJSONObject("dominant").getString("hex"));
                        r = Float.parseFloat(obj.getJSONObject("colors").getJSONObject("dominant").getString("r"));
                        g = Float.parseFloat(obj.getJSONObject("colors").getJSONObject("dominant").getString("g"));
                        b = Float.parseFloat(obj.getJSONObject("colors").getJSONObject("dominant").getString("b"));

                        JSONObject accentObj;

                        try {
                            accentObj = obj.getJSONObject("colors").getJSONArray("accent").getJSONObject(0);
                        } catch (Exception e) {
                            accentObj = obj.getJSONObject("colors").getJSONArray("other").getJSONObject(0);
                        }

                        hex2 = (accentObj.getString("hex"));
                        r2 = Float.parseFloat(accentObj.getString("r"));
                        g2 = Float.parseFloat(accentObj.getString("g"));
                        b2 = Float.parseFloat(accentObj.getString("b"));

                        c[0] = new Color(r, g, b, r2, g2, b2, hex1, hex2);

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

        return c[0];

    }

    private final String TAG = "ConfirmToWardrobe";

    private FireBaseManager fb_manager;
    private Clothes_Factory clothes_factory;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "Entering onCreate for ConfirmToWardrobe");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_to_wardrobe);

        clothes_factory = new Clothes_Factory();
        fb_manager = FireBaseManager.getInstance();
        boolean edit = false;
        Clothes oldClothes = null;

        ImageView editItemImage = (ImageView) findViewById(R.id.editItemImage);
        Button confirm = (Button) findViewById(R.id.ConfirmAdd);
        clothingTypes = (RadioGroup) findViewById(R.id.clothingTypes);
        waterproofSetting = (RadioGroup) findViewById(R.id.waterproof_group);
        mainCol = (View) findViewById(R.id.colorMain);
        accCol = (View) findViewById(R.id.colorAcc);

        ArrayList<String> fileNames = getIntent().getStringArrayListExtra("fileNames");
        String itemId = getIntent().getStringExtra("itemId");

        if(fileNames != null) { // IF PHOTOS ARE BEING SUBMITTED FROM ADDTOWARDROBE
            edit = false;
            System.out.println(fileNames.size() + " submitted");
            rmBackground(fileNames.get(0));
            color = extractColor(fileNames.get(0));

            if(color != null){
                mainCol.setBackgroundColor(android.graphics.Color.parseColor(color.getHex1()));
                accCol.setBackgroundColor(android.graphics.Color.parseColor(color.getHex2()));
            }
        } else { //IF PHOTO IS BEING SUBMITTED FOR EDIT
            edit = true;
            oldClothes = fb_manager.getUser().getWardrobe().getClothesByUid(itemId);
            System.out.println(itemId + " submitted for EDIT");

            StorageReference pathReference = FirebaseStorage.getInstance().getReference();

            Clothes finalOldClothes = oldClothes;
            pathReference.child(oldClothes.getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    try{
                        byte[] bytes = task.getResult();
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        editItemImage.setImageBitmap(b);
                        if(finalOldClothes.getColor() != null){
                            mainCol.setBackgroundColor(android.graphics.Color.parseColor(finalOldClothes.getColor().getHex1()));
                            accCol.setBackgroundColor(android.graphics.Color.parseColor(finalOldClothes.getColor().getHex2()));
                        }
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Unable to parse image",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        TextView typeLabel = new TextView(getApplicationContext());
        typeLabel.setText("Choose the type:");
        clothingTypes.addView(typeLabel, 0);

        String[] types = Clothes.getTypes(Clothes.Type.class);
        for (int i = 0; i < types.length; i++) {
            RadioButton current = new RadioButton(this);
            current.setText(types[i]);
            clothingTypes.addView(current, i+1);
            if(edit && types[i].equals(oldClothes.getType().toString())) {
                clothingTypes.check(clothingTypes.getChildAt(i+1).getId());
            }
        }

        clothingTypes.setGravity(Gravity.LEFT);

        if(!edit) {
            clothingTypes.check(clothingTypes.getChildAt(1).getId());
        } else {
            if(oldClothes.isWaterResistant()) {
                waterproofSetting.check(waterproofSetting.getChildAt(2).getId());
            }
        }


        boolean finalEdit = edit;
        Clothes finalOldClothes1 = oldClothes;
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = clothingTypes.getCheckedRadioButtonId();
                int selectedWater = waterproofSetting.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radio_button = (RadioButton) findViewById(selectedId);
                RadioButton waterproofButton = (RadioButton) findViewById(selectedWater);
                Log.d(TAG, (String) radio_button.getText());

                if(finalEdit) { //IF EDIT

                    finalOldClothes1.setType(Clothes.Type.valueOf(radio_button.getText().toString()));

                    if (waterproofButton.getText().equals("Yes")) { //Update water resistance with new val
                        finalOldClothes1.setWaterResistant(true);
                    } else {
                        finalOldClothes1.setWaterResistant(false);
                    }
                    System.out.println(finalOldClothes1.toString());
                    fb_manager.updateEditedClothes();

                    finish();
                } else { //IF ADDING NEW CLOTHES ITEM
                    //1. Add submission to database TODO

                    Clothes new_clothes = getClothes(String.valueOf(radio_button.getText()));

                    storage = FirebaseStorage.getInstance();
                    storageRef = storage.getReference();

                    String[] spl = fileNames.get(0).split("/");
                    String newName = spl[spl.length - 1].split("\\.")[0] + ".png";
                    StorageReference picRef = storageRef.child(newName);
                    picRef.putFile(Uri.fromFile(new File(fileNames.get(0))));
                    System.out.println("IMAGE ADDED TO DB");

                    if (waterproofButton.getText().equals("Yes")) {
                        new_clothes.setWaterResistant(true);
                    } else {
                        new_clothes.setWaterResistant(false);
                    }
                    new_clothes.setImageURL(newName);
                    new_clothes.setColor(color);

                    if (new_clothes == null) {
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


    private Clothes getClothes(String type){
        try{
            Clothes.Type clothes_type = Clothes.Type.valueOf(type);
            switch(clothes_type){
                case HEAVY_JACKET:
                    return clothes_factory.get_heavyjacket();
                case LIGHT_JACKET:
                    return clothes_factory.get_light_jacket();
                case T_SHIRT:
                    return clothes_factory.get_tshirt();
                case LONG_SLEEVE:
                    return clothes_factory.get_long_sleeve();
                case SHOES:
                    return clothes_factory.get_shoes();
                case PANTS:
                    return clothes_factory.get_pants();
                case SHORTS:
                    return clothes_factory.get_shorts();
                case SWEATER:
                    return clothes_factory.get_sweater();
                default:
                    // should never get to this point unless we add now types
                    return clothes_factory.get_long_sleeve();
            }
        } catch (IllegalArgumentException e){
            // shouldn't happen since we create the radio button text using the enum list
            Log.e(TAG, "Unable to convert stirng to clothes types");
        }

        return null;
    }
    
    //https://stackoverflow.com/questions/63410194/how-to-save-multiple-bitmaps-fastly-in-android-studio
    public void saveBitmap(Bitmap output, String fileName){
        output = TrimImage(output);
        System.out.println("Saved to " + fileName);
        File image = new File(fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(image);
            output.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
            compress(output, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println("ERROR SAVING IMAGE: " + e);
        }
    }

    //https://stackoverflow.com/questions/7385616/crop-trim-a-jpg-file-with-empty-space-with-java
    public static Bitmap TrimImage(Bitmap bmp) {
        int imgHeight = bmp.getHeight();
        int imgWidth  = bmp.getWidth();

        //TRIM WIDTH
        int widthStart  = imgWidth;
        int widthEnd = 0;
        for(int i = 0; i < imgHeight; i++) {
            for(int j = imgWidth - 1; j >= 0; j--) {
                if(bmp.getPixel(j, i) != android.graphics.Color.TRANSPARENT &&
                        j < widthStart) {
                    widthStart = j;
                }
                if(bmp.getPixel(j, i) != android.graphics.Color.TRANSPARENT &&
                        j > widthEnd) {
                    widthEnd = j;
                    break;
                }
            }
        }
        //TRIM HEIGHT
        int heightStart = imgHeight;
        int heightEnd = 0;
        for(int i = 0; i < imgWidth; i++) {
            for(int j = imgHeight - 1; j >= 0; j--) {
                if(bmp.getPixel(i, j) != android.graphics.Color.TRANSPARENT &&
                        j < heightStart) {
                    heightStart = j;
                }
                if(bmp.getPixel(i, j) != android.graphics.Color.TRANSPARENT &&
                        j > heightEnd) {
                    heightEnd = j;
                    break;
                }
            }
        }

        int finalWidth = widthEnd - widthStart;
        int finalHeight = heightEnd - heightStart;

        return Bitmap.createBitmap(bmp, widthStart,heightStart,finalWidth, finalHeight);
    }

    // private method to compress the image to at least under 1MB
    private void compress(Bitmap image, FileOutputStream output_stream){

        if(image == null){
            Toast.makeText(ConfirmToWardrobe.this, "[+] Error! Unable to compress image. Please try again",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int size = image.getByteCount();
        int compression = 40;
        while(size >= MAX_IMAGE_SIZE){
            image.compress(Bitmap.CompressFormat.PNG, compression, output_stream);
            size = image.getByteCount();
            Log.e(TAG, "Compressing image: " + size);
            compression /= 2;
        }
        Log.e(TAG, "Size of image: " + size);
    }
}
