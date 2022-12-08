package com.cs501.project;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.cs501.project.Model.Wardrobe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewOutfit extends AppCompatActivity {

    // initialize members
    private final static String TAG = "ViewOutfit";
    private FireBaseManager fb_manager;

    private ListView lvOutfits;
    ViewOutfitAdapter lvAdapter;
    private TextView noneThereOutfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_outfit);

        noneThereOutfit = (TextView) findViewById(R.id.noneThereOutfit);
        noneThereOutfit.setVisibility(View.GONE);

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Have our listview auto update when database changes
        myRef.child(currentUser.getUid()).child("users").child(String.valueOf(fb_manager.getUserIdx())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                User user = dataSnapshot.getValue(User.class);

                // update list
                lvOutfits = (ListView)findViewById(R.id.lvOutfits);
                Wardrobe wardrobeNeeded = fb_manager.getWardrobe();
                lvAdapter = new ViewOutfitAdapter(ViewOutfit.this, user.getWardrobe().getOutfits(), height, width, wardrobeNeeded, fb_manager);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                lvOutfits.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
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

}

// custom adapter
class ViewOutfitAdapter extends BaseAdapter {

    ArrayList<Clothes> clothesList;
    ArrayList<Outfit> outfits;
    SharedPreferences sharedPreferences;
    Context context;
    ViewOutfitAdapter adapter;
    Wardrobe wardrobeNeeded;;
    int height, width;
    FireBaseManager fb_manager;

    public ViewOutfitAdapter(Context aContext, ArrayList<Outfit> outfits, int height, int width,  Wardrobe wardrobeNeeded, FireBaseManager fb_manager) {
        //initializing our data in the constructor.
        context = aContext;

        if(outfits == null || outfits.size() <= 0){
            this.outfits = new ArrayList<Outfit>();
        } else{
            this.outfits = outfits;
        }
        this.adapter=this;
        this.height = height;
        this.width = width;
        this.wardrobeNeeded = wardrobeNeeded;
        this.fb_manager = fb_manager;
        sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return outfits.size();
    }

    @Override
    public Object getItem(int position) {
        return outfits.get(position);
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
            row = inflater.inflate(R.layout.outfit_list_view, parent, false);

        } else {
            row = convertView;
        }

        LinearLayout.LayoutParams lp  = new  LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.LEFT;
        row.setLayoutParams(lp);

        Outfit outfit_view = outfits.get(position);
        TextView outfit_name = (TextView) row.findViewById(R.id.outfit_new_text);
        outfit_name.setText(outfit_view.getName());
        LinearLayout singularOutfit = (LinearLayout) row.findViewById(R.id.outfitClothes);

        ArrayList<String> clothes_in_outfit = outfit_view.getOutfit();

        int clothingHeight = height/10;
        int clothingWidth = 170*clothes_in_outfit.size() >= width ? width/clothes_in_outfit.size() : 170;

        System.out.println(clothingHeight + " " + clothingWidth);

        for(int i = 0; i < clothes_in_outfit.size(); i++){
            String uid = clothes_in_outfit.get(i);
            Clothes temp  = wardrobeNeeded.getClothesByUid(uid);
            ImageView img = new ImageView(context);
            img.setLayoutParams(new LinearLayout.LayoutParams(clothingWidth, clothingHeight));

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    StorageReference pathReference = FirebaseStorage.getInstance().getReference();
                    pathReference.child(temp.getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            try{
                                byte[] bytes = task.getResult();
                                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                img.setImageBitmap(b);
                            } catch (Exception e){
                                Toast.makeText(context, "Unable to parse image for the clothing. Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            singularOutfit.addView(img);
        }

        Button delete = (Button) row.findViewById(R.id.button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clothes.remove(position);
                fb_manager.deleteOutFit(outfit_view.getOutfitUniqueId());
                Toast.makeText(context, "Deleted item " + position + ".",
                        Toast.LENGTH_SHORT).show();
                //adapter.notifyDataSetChanged();
            }
        });

        return row;
    }
}