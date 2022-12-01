package com.cs501.project;

import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs501.project.Model.Clothes;
import com.cs501.project.Model.FireBaseManager;
import com.cs501.project.Model.Profile;
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

public class ViewWardrobe extends AppCompatActivity {

    private final static String TAG = "ViewWardrobe";
    private FireBaseManager fb_manager;

    private ListView lvClothes;     //Reference to the listview GUI component
    MyCustomAdapter lvAdapter;   //Reference to the Adapter used to populate the listview.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wardrobe);

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

                lvAdapter = new MyCustomAdapter(ViewWardrobe.this, fb_manager.getClothes());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                lvClothes.setAdapter(lvAdapter);
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
class MyCustomAdapter extends BaseAdapter {

    ArrayList<Clothes> clothes;
    SharedPreferences sharedPreferences;
    Context context;
    MyCustomAdapter adapter;

    public MyCustomAdapter(Context aContext, ArrayList<Clothes> clothes) {
        //initializing our data in the constructor.
        context = aContext;

        if(clothes == null || clothes.size() <= 0){
            this.clothes = new ArrayList<Clothes>();
        } else{
            this.clothes = clothes;
        }
        this.adapter=this;
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
        Button delete = (Button) row.findViewById(R.id.delete_button);

        Clothes clothes_view = clothes.get(position);

        // everything we add to view should be in the try catch
        try {
            clothes_type.setText(clothes_view.getType().name());
            clothes_id.setText(clothes_view.getUniqueId());
        } catch (Exception e){
            Toast.makeText(context, "Unable to parse data for the " + position + " clothing. Please try again",
                    Toast.LENGTH_SHORT).show();
        }

        try {
            String hex1 = clothes_view.getColor().getHex1();
            String hex2 = clothes_view.getColor().getHex2();

            color1.setBackgroundColor(Color.parseColor(hex1));
            color2.setBackgroundColor(Color.parseColor(hex2));
        } catch (Exception e) {
            Toast.makeText(context, "Unable to parse color data for the " + position + " clothing.",
                    Toast.LENGTH_SHORT).show();
        }

        // get image
        StorageReference pathReference = FirebaseStorage.getInstance().getReference();
        pathReference.child(clothes_view.getImageURL()).getBytes(ConfirmToWardrobe.MAX_IMAGE_SIZE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                try{
                    byte[] bytes = task.getResult();
                    Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(b);
                } catch (Exception e){
                    Toast.makeText(context, "Unable to parse image for the " + position + " clothing. Please try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences sh = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clothes.remove(position);
                FireBaseManager.getInstance().deleteItem(clothes_view.getUniqueId());
                Toast.makeText(context, "Deleted item " + position + ".",
                        Toast.LENGTH_SHORT).show();
                //adapter.notifyDataSetChanged();
            }
        });

        return row;
    }
}