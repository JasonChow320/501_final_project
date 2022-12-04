package com.cs501.project.Model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
/**
 * Wardrobe class contains all clothes for the user
 */
public class Wardrobe {
    ArrayList<Clothes> clothes;
    ArrayList<Outfit> outfits;

    private final static String TAG = "Wardrobe";

    public Wardrobe(){
        this.clothes = new ArrayList<Clothes>();
        this.outfits = new ArrayList<Outfit>();
    }

    public ArrayList<Outfit> getOutfits(){
        return this.outfits;
    }

    public void addOutfits(ArrayList<Outfit> outfits){

        if(outfits != null){
            this.outfits = outfits;
        }
    }

    public boolean insertClothes(Clothes clothing){
        if(clothing == null){
            return false;
        }

        this.clothes.add(clothing);
        return true;
    }

    public boolean deleteItem(String uid) {
        for (Clothes c: clothes) {
            if(c.getUniqueId().equals(uid)) {
                clothes.remove(c);
                return true;
            }
        }
        return false;
    }

    // private to get clothes by its unique id
    public Clothes getClothesByUid(String uid){

        Clothes clothes_return = null;

        Log.d("Wardrobe", "uid: " + uid);
        for(Clothes clothing : this.clothes){
            Log.d("Wardrobe", "clothes' uid: " + clothing.getUniqueId());
            if(clothing.getUniqueId().equals(uid)){
                Log.d("Wardrobe", "found id: " + uid);
                clothes_return = clothing;
            }
        }

        // we're okay with returning null
        return clothes_return;
    }

    @Exclude
    public boolean addOutfitToWardRobe(Outfit outfit){

        if(outfit == null){
            return false;
        }

        // check for duplicate
        for(Outfit o : this.outfits){
            if(o.compareTo(outfit) == 0){
                Log.d(TAG, "Duplicate outfit found");
                return false;
            }
        }

        this.outfits.add(outfit);

        return true;
    }

    public ArrayList<Clothes> getClothes(){
        return this.clothes;
    }

    @Exclude
    public ArrayList<Clothes> getTShirts(){
        return this.getType(Clothes.Type.T_SHIRT);
    }

    @Exclude
    public ArrayList<Clothes> getShirt(){
        return this.getType(Clothes.Type.LONG_SLEEVE);
    }

    @Exclude
    public ArrayList<Clothes> getShoes(){
        return this.getType(Clothes.Type.SHOES);
    }

    @Exclude
    public ArrayList<Clothes> getPants(){
        return this.getType(Clothes.Type.PANTS);
    }

    @Exclude
    public ArrayList<Clothes> getShorts(){
        return this.getType(Clothes.Type.SHORTS);
    }

    @Exclude
    public ArrayList<Clothes> getLightJackets(){
        return this.getType(Clothes.Type.LIGHT_JACKET);
    }

    @Exclude
    public ArrayList<Clothes> getHeavyJackets(){
        return this.getType(Clothes.Type.heavy_jacket);
    }

    private ArrayList<Clothes> getType(Clothes.Type type){
        ArrayList<Clothes> ret = new ArrayList<Clothes>();
        for(Clothes clothing : this.clothes){
            if(clothing.getType() == type){
                ret.add(clothing);
            }
        }

        return ret;
    }
}
