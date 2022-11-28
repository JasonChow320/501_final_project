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

        fb_manager = FireBaseManager.getInstance();

        lvClothes = (ListView)findViewById(R.id.lvClothes);

        lvAdapter = new MyCustomAdapter(this.getBaseContext(), fb_manager.getClothes());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
        lvClothes.setAdapter(lvAdapter);
        Context mContext=getApplicationContext();
    }
}

// custom adapter
class MyCustomAdapter extends BaseAdapter {

    ArrayList<Clothes> clothes;
    SharedPreferences sharedPreferences;
    Context context;

    public MyCustomAdapter(Context aContext, ArrayList<Clothes> clothes) {
        //initializing our data in the constructor.
        context = aContext;

        if(clothes == null || clothes.size() <= 0){
            this.clothes = new ArrayList<Clothes>();
        } else{
            this.clothes = clothes;
        }

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

        Clothes clothes_view = clothes.get(position);

        clothes_type.setText(clothes_view.getType().name());
        clothes_id.setText(clothes_view.getUniqueId());

        StorageReference pathReference = FirebaseStorage.getInstance().getReference();
        pathReference.child(clothes_view.getImageURL()).getBytes(1000000).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                byte[] bytes = task.getResult();
                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(b);
            }
        });

        SharedPreferences sh = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);

        return row;
    }
}