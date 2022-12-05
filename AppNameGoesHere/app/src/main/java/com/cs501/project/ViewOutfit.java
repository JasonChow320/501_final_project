package com.cs501.project;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

                lvAdapter = new ViewOutfitAdapter(ViewOutfit.this, user.getWardrobe().getOutfits());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                lvOutfits.setAdapter(lvAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}

// custom adapter
class ViewOutfitAdapter extends BaseAdapter {

    ArrayList<Outfit> outfits;
    SharedPreferences sharedPreferences;
    Context context;
    ViewOutfitAdapter adapter;

    public ViewOutfitAdapter(Context aContext, ArrayList<Outfit> outfits) {
        //initializing our data in the constructor.
        context = aContext;

        if(outfits == null || outfits.size() <= 0){
            this.outfits = new ArrayList<Outfit>();
        } else{
            this.outfits = outfits;
        }
        this.adapter=this;
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
            row = inflater.inflate(R.layout.listview_row, parent, false);
        } else {
            row = convertView;
        }

        return row;
    }
}