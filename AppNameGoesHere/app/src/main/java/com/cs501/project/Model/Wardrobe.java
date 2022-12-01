package com.cs501.project.Model;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
/**
 * Wardrobe class contains all clothes for the user
 */
public class Wardrobe {
    ArrayList<Clothes> clothes;

    public Wardrobe(){
        this.clothes = new ArrayList<Clothes>();
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

    public ArrayList<Clothes> getClothes(){
        return this.clothes;
    }

    @Exclude
    public ArrayList<Clothes> getTShirts(){
        return this.getType(Clothes.Type.t_shirt);
    }

    @Exclude
    public ArrayList<Clothes> getShirt(){
        return this.getType(Clothes.Type.shirt);
    }

    @Exclude
    public ArrayList<Clothes> getShoes(){
        return this.getType(Clothes.Type.shoes);
    }

    @Exclude
    public ArrayList<Clothes> getPants(){
        return this.getType(Clothes.Type.pants);
    }

    @Exclude
    public ArrayList<Clothes> getShorts(){
        return this.getType(Clothes.Type.shorts);
    }

    @Exclude
    public ArrayList<Clothes> getJackets(){
        return this.getType(Clothes.Type.jacket);
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
