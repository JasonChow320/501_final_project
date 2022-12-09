package com.cs501.project.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;

/*
    The FireBaseManager class controls the data flow between the Realtime database
    and the application profile's class.
    This class uses the Singleton design pattern
 */
public class FireBaseManager {

    // create private Singleton object
    private static FireBaseManager manager_instance;

    // private methods
    private final static String TAG = "FireBaseManager";

    // Initialize Firebase Auth and Profile
    private FirebaseAuth mAuth;
    private Profile user;
    private int user_idx;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser currentUser;

    private String image_path;

    // private constructor only for getInstance
    private FireBaseManager() {

        mAuth = FirebaseAuth.getInstance();
        this.user = new Profile();

        // retrieve account database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("accounts");
        currentUser = mAuth.getCurrentUser();

        image_path = new String();

        user_idx = 0;

        Log.d(TAG, "Signed in as user: " + currentUser.getUid());

        // Read from the database
        myRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "Got data from database");
                Profile account = dataSnapshot.getValue(Profile.class);
                if(account != null){
                    Log.d(TAG, "Value is: " + account.toString());
                }
                user = account;
                saveCache();
            }

            @Override
            public void onCancelled(DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // static get instance method
    public static FireBaseManager getInstance() {

        if (manager_instance == null) {
            manager_instance = new FireBaseManager();
        }
        return manager_instance;
    }

    public void setImagePath(String path){

        if(path == null){
            return;
        }
        this.image_path = path;
    }

    public void setEnableCache(int value){

        User user = this.getUser();
        user.getUserSettings().setEnableCache(value);
        this.update();
    }

    private void saveCache(){

        for(User user : this.user.getUsers()){
            if(user.getUserSettings().getEnableCache() == 1){
                for(Clothes c : user.getWardrobe().getClothes()){

                    // enable cache, download all images
                    File dir = new File(this.image_path, c.getImageURL());

                    if(dir.getAbsoluteFile().exists()){

                        System.out.println("Found file_path: " + this.image_path + "/" + c.getImageURL());
                    } else {
                        System.out.println("Does not exist");

                        // get from firebase storage
                    }
                }
            }
        }
    }

    public void deleteCache(){
        File directory = new File(this.image_path);
        File[] files = directory.listFiles();

        if(files == null){
            return;
        }
        System.out.println("File Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            System.out.println("Files FileName:" + files[i].getName());

            if(files[i].getName().contains(".png") || files[i].getName().contains(".jpg")){
                File file_to_delete = new File(this.image_path, files[i].getName());

                if (file_to_delete.delete()) {
                    System.out.println("Deleted the file: " + file_to_delete.getName());
                } else {
                    System.out.println("Failed to delete the file.");
                }
            }
        }
    }

    public Profile getProfile(){
        return this.user;
    }

    public void setProfile(Profile user){

        if(user == null){
            return;
        }

        this.user = user;
        return;
    }

    public void addClothes(Clothes new_clothes){

        if(new_clothes == null){
            Log.d(TAG, "Unable to add null object to wardrobe");
            return;
        }
        Log.d(TAG, "Adding clothes: " + new_clothes.toString());

        Wardrobe user_wardrobe = user.getUsers().get(user_idx).getWardrobe();
        user_wardrobe.insertClothes(new_clothes);

        myRef.child(currentUser.getUid()).setValue(user);

        return;
    }

    public void updateEditedClothes() {
        myRef.child(currentUser.getUid()).setValue(user);
        return;
    }

    public void deleteItem(String uid) {
        Wardrobe user_wardrobe = user.getUsers().get(user_idx).getWardrobe();

        // Get item to delete
        Log.d(TAG, "uid: " + uid);
        Clothes clothes_to_delete = user_wardrobe.getClothesByUid(uid);

        if(clothes_to_delete == null){
            Log.d(TAG, "Cannot find clothing with the unique id");
            return;
        }

        // delete image from storage database
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference imageRef = storageRef.child(clothes_to_delete.getImageURL());

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });

        // delete image from user realtime database
        user_wardrobe.deleteItem(uid);

        myRef.child(currentUser.getUid()).setValue(user);

        return;
    }

    public void deleteOutFit(String uid) {
        Wardrobe user_wardrobe = user.getUsers().get(user_idx).getWardrobe();

        // Get item to delete
        Log.d(TAG, "uid: " + uid);
        Outfit outfit_to_delete = user_wardrobe.getOutfitByUid(uid);

        if(outfit_to_delete == null){
            Log.d(TAG, "Cannot find outfit with the unique id");
            return;
        }

        // delete image from storage database
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete

        // delete image from user realtime database
        user_wardrobe.deleteOutfitByUid(uid);

        myRef.child(currentUser.getUid()).setValue(user);

        return;
    }

    public User getUser(){

        ArrayList<User> users = user.getUsers();
        Log.d(TAG, "Getting clothes from FireBaseManager: user_idx: " + user_idx + ", users size: " + users.size());

        if(users.size() <= user_idx){
            return null;
        }

        User user = users.get(user_idx);

        Log.d(TAG, "The user is: " + user);
        return user;
    }

    public ArrayList<Clothes> getClothes(){

        ArrayList<User> users = user.getUsers();
        Log.d(TAG, "Getting clothes from FireBaseManager: user_idx: " + user_idx + ", users size: " + users.size());

        if(users.size() <= user_idx){
            return null;
        }

        User user = users.get(user_idx);
        Wardrobe user_wardrobe = user.getWardrobe();
        ArrayList<Clothes> clothes = user_wardrobe.getClothes();

        Log.d(TAG, "The clothes is: " + clothes);
        return clothes;
    }

    public void updateOneLayerTemp(int temp){

        User user = this.getUser();
        if(user == null){
            return;
        }
        User_settings settings = user.getUserSettings();
        settings.setOneLayerTemp(temp);

        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public void updateThreeLayerTemp(int temp){

        User user = this.getUser();
        if(user == null){
            return;
        }

        User_settings settings = user.getUserSettings();
        settings.setThreeLayerTemp(temp);

        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public void updateFlashMode(String temp){

        User user = this.getUser();
        if(user == null){
            return;
        }

        User_settings settings = user.getUserSettings();
        settings.setFlashMode(temp);

        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public void updateTheme(int temp){

        User user = this.getUser();
        if(user == null){
            return;
        }

        User_settings settings = user.getUserSettings();
        settings.setTheme(temp);

        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public void updateLanguage(String temp){

        User user = this.getUser();
        if(user == null){
            return;
        }

        User_settings settings = user.getUserSettings();
        settings.setLanguage(temp);

        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public int getUserIdx(){
        return this.user_idx;
    }

    // set which user we're using
    public void setUserIdx(int userIdx){

        ArrayList<User> users = user.getUsers();
        if(userIdx < 0 || userIdx >= users.size()){
            Log.e(TAG, "Unable to add userIdx to FireBaseManager, invalid idx value: " + userIdx);
            return;
        }

        this.user_idx = userIdx;
        return; // thx Android Studio
    }

    // api to update the database
    public void update(){
        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    // add user to firebase
    public void addUser(User user){

        this.user.addUser(user);
        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    // delete user to firebase
    public void deleteUser(int user_idx){

        this.user.deleteUser(user_idx);
        myRef.child(currentUser.getUid()).setValue(this.user);
    }

    public boolean addOutfit(Outfit new_outfit){

        if(new_outfit == null){
            return false;
        }

        User user = this.getUser();
        if(user == null){
            return false;
        }

        Wardrobe wardrobe = user.getWardrobe();

        // check if we have a duplicate
        if(!wardrobe.addOutfitToWardRobe(new_outfit)){
            // got duplicate
            return false;
        }

        // save to database
        myRef.child(currentUser.getUid()).setValue(this.user);
        return true;
    }

    public Wardrobe getWardrobe(){
        ArrayList<User> users = user.getUsers();
        Log.d(TAG, "Getting clothes from FireBaseManager: user_idx: " + user_idx + ", users size: " + users.size());

        if(users.size() <= user_idx){
            return null;
        }

        User user = users.get(user_idx);
        Wardrobe user_wardrobe = user.getWardrobe();
        return user_wardrobe;
    }
}
