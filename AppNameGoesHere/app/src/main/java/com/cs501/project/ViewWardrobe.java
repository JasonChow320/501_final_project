package com.cs501.project;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Outfit;
import com.cs501.project.Model.Profile;
import com.cs501.project.Model.User;
import com.cs501.project.Model.User_settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ViewWardrobe extends AppCompatActivity {

    private final static String TAG = "ViewWardrobe";
    private FireBaseManager fb_manager;

    private ListView lvClothes;     //Reference to the listview GUI component
    MyCustomAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.
    Spinner clothingChoices;
    TextView noneThere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wardrobe);

        noneThere = (TextView) findViewById(R.id.noClothesAvalible);
        noneThere.setVisibility(View.GONE);
        // Get FireBaseManager singleton object and initialize ListView for user's wardrobe
        fb_manager = FireBaseManager.getInstance();
        // Get account's users
        //fb_manager = FireBaseManager.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // retrieve account database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("accounts");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG, "Signed in as user: " + currentUser.getUid());

        // Initialize our firebase manager
        fb_manager = FireBaseManager.getInstance();

        String deletionMessage = getResources().getString(R.string.ClothingDeletionMessage);
        String confirm = getResources().getString(R.string.confirm);
        String confirmMsg = getResources().getString(R.string.confirmClothingDeletion);
        // Have our listview auto update when database changes
        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                Profile profile = dataSnapshot.getValue(Profile.class);

                // update list
                lvClothes = (ListView)findViewById(R.id.lvClothes);

                lvAdapter = new MyCustomAdapter(ViewWardrobe.this, fb_manager.getClothes(), deletionMessage, confirm, confirmMsg);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                lvClothes.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //Initialize spinner
        clothingChoices = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.viewWardrobeSpinner ,android.R.layout.simple_spinner_item);
        clothingChoices.setAdapter(adapter);
        clothingChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(getClothes(i).size() == 0) {
                    if(i == 0){
                        noneThere.setText(getResources().getString(R.string.none_available_all));

                    } else {
                        noneThere.setText(getResources().getString(R.string.none_available));

                    }
                    noneThere.setVisibility(View.VISIBLE);
                    lvClothes.setAdapter(null);
                } else {
                    lvAdapter = new MyCustomAdapter(ViewWardrobe.this, getClothes(i), deletionMessage, confirm, confirmMsg);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                    lvClothes.setAdapter(lvAdapter);
                    noneThere.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                lvAdapter = new MyCustomAdapter(ViewWardrobe.this, getClothes(0) , deletionMessage, confirm, confirmMsg);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                lvClothes.setAdapter(lvAdapter);
            }
        });
    }

    public void onStart() {
        super.onStart();
        User_settings uSettings = fb_manager.getUser().getUserSettings();
        ConstraintLayout con = findViewById(R.id.background);
        String backgroundColor = getResources().getStringArray(R.array.themesValues)[uSettings.getTheme()];
        System.out.println(backgroundColor);
        con.setBackgroundColor(Color.parseColor(backgroundColor));
    }


    public ArrayList<Clothes> getClothes(Integer type){
        switch(type){
            case 1: //T shirt
                return fb_manager.getUser().getWardrobe().getTShirts();
            case 2: //Shirt
                return fb_manager.getUser().getWardrobe().getLongSleeve();
            case 3: //Shorts
                return fb_manager.getUser().getWardrobe().getShorts();
            case 4: //Pants
                return fb_manager.getUser().getWardrobe().getPants();
            case 5: //Shoes
                return fb_manager.getUser().getWardrobe().getShoes();
            case 6: //Sweaters
                return fb_manager.getUser().getWardrobe().getSweater();
            case 7: //LightJackets
                return fb_manager.getUser().getWardrobe().getLightJackets();
            case 8: //Heavy Jackets
                return fb_manager.getUser().getWardrobe().getHeavyJackets();
            default:
                return fb_manager.getClothes();
        }
    };

}

// custom adapter
class MyCustomAdapter extends BaseAdapter {

    ArrayList<Clothes> clothes;
    SharedPreferences sharedPreferences;
    Context context;
    MyCustomAdapter adapter;
    String deletionMsg;
    String confirm;
    String confirmMsg;
    FireBaseManager fb_manager;

    public MyCustomAdapter(Context aContext, ArrayList<Clothes> clothes, String deletionMsg, String confirm, String confirmMsg ) {
        //initializing our data in the constructor.
        context = aContext;

        if(clothes == null || clothes.size() <= 0){
            this.clothes = new ArrayList<Clothes>();
        } else{
            this.clothes = clothes;
        }
        this.fb_manager = FireBaseManager.getInstance();
        this.adapter=this;
        this.deletionMsg = deletionMsg;
        this.confirm = confirm;
        this.confirmMsg = confirmMsg;
        sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return clothes.size();
    }

    @Override
    public Object getItem(int position) {
        return clothes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_row, parent, false);
        } else {
            row = convertView;
        }

        TextView clothes_type = (TextView) row.findViewById(R.id.clothes_type_view);
        TextView clothes_id = (TextView) row.findViewById(R.id.clothes_id_view);
        ImageView image = (ImageView) row.findViewById(R.id.clothes_image);
        View color1 = (View) row.findViewById(R.id.color1);
        View color2 = (View) row.findViewById(R.id.color2);
        Button edit = (Button) row.findViewById(R.id.edit_button);
        Button delete = (Button) row.findViewById(R.id.delete_button);
        ImageView waterIndicator = (ImageView) row.findViewById(R.id.waterproofIndicator);

        Clothes clothes_view = clothes.get(position);

        if(clothes_view.isWaterResistant()) {
            waterIndicator.setVisibility(View.VISIBLE);
        } else {
            waterIndicator.setVisibility(View.INVISIBLE);
        }

        // everything we add to view should be in the try catch
        try {
            clothes_type.setText(getDisplayName(clothes_view.getType()));
            clothes_id.setText(clothes_view.getUniqueId());
        } catch (Exception e){
            Toast.makeText(context, context.getString(R.string.fail_data_start) + position + context.getString(R.string.clothing_period),
                    Toast.LENGTH_SHORT).show();
        }

        try {
            String hex1 = clothes_view.getColor().getHex1();
            String hex2 = clothes_view.getColor().getHex2();

            color1.setBackgroundColor(Color.parseColor(hex1));
            color2.setBackgroundColor(Color.parseColor(hex2));
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.fail_color_data) + position + context.getString(R.string.clothing_period),
                    Toast.LENGTH_SHORT).show();
        }

        // get image
        User user = fb_manager.getUser();
        boolean displayImg = false, cache = (user.getUserSettings().getEnableCache() == 1);

        if(cache) {

            Clothes clothes = this.clothes.get(position);
            System.out.println("Trying to decode: " + fb_manager.getImagePath() + "/" + clothes.getImageURL());
            if (clothes == null) {
                Log.d("ViewWardRobeAdapter", "ViewWardrobe: Tried to get clothes at an invalid position");
            } else {
                Bitmap b = BitmapFactory.decodeFile(fb_manager.getImagePath() + "/" + clothes.getImageURL());
                if (b == null) {
                    System.out.println("Can't decode image");
                } else {
                    image.setImageBitmap(b);
                    System.out.println("Displaying image from cache");
                    displayImg = true;
                }
            }
        }

        if(!displayImg){

            // retrieve image from database
            StorageReference pathReference = FirebaseStorage.getInstance().getReference();
            pathReference.child(clothes_view.getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    try{
                        byte[] bytes = task.getResult();
                        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.setImageBitmap(b);

                        if(cache){
                            fb_manager.saveBitmap(b, fb_manager.getImagePath() + "/" + clothes_view.getImageURL());
                            System.out.println("Adding to cache for cache enabled user");
                        }
                    } catch (Exception e){
                        Toast.makeText(context, context.getString(R.string.fail_image_data) + position + context.getString(R.string.clothing_period),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        SharedPreferences sh = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clothes.remove(position);
                onDeleteActions(clothes_view.getUniqueId(), position);
                //adapter.notifyDataSetChanged();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ConfirmToWardrobe.class);
                i.putExtra("itemId", clothes_view.getUniqueId());
                context.startActivity(i);
                //adapter.notifyDataSetChanged();
            }
        });

        return row;
    }

    public void onDeleteActions(String uID, int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(confirmMsg);
        builder.setMessage(deletionMsg);
        builder.setPositiveButton(confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> outfitsForDeletion = new ArrayList<>();
                        ArrayList<Outfit> outfits = fb_manager.getUser().getWardrobe().getOutfits();
                        for(Outfit outfit: outfits){
                            ArrayList<String> clothes = outfit.getOutfit();
                            for(String clothingID: clothes){
                                if(clothingID.equals(uID)){
                                    outfitsForDeletion.add(outfit.getOutfitUniqueId());
                                }
                            }
                        }

                        for(String uid: outfitsForDeletion){
                            fb_manager.deleteOutFit(uid);
                        }

                        FireBaseManager.getInstance().deleteItem(uID);
                        Toast.makeText(context, context.getString(R.string.del_item) + position + ".",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog confirm_dialog = builder.create();
        confirm_dialog.show();
    }

    public String getDisplayName(Clothes.Type t) {
        switch(t) {
            case T_SHIRT:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[1];
            case LONG_SLEEVE:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[2];
            case SHORTS:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[3];
            case PANTS:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[4];
            case SHOES:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[5];
            case SWEATER:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[6];
            case LIGHT_JACKET:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[7];
            case HEAVY_JACKET:
                return context.getResources().getStringArray(R.array.viewWardrobeSpinner)[8];
        }
        return "error";
    }
}
