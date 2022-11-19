package com.cs501.project.Model;

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

    public ArrayList<Clothes> getClothes(){
        return this.clothes;
    }

    public ArrayList<Clothes> getTShirts(){
        return this.getType(Clothes.Type.t_shirt);
    }

    public ArrayList<Clothes> getShirt(){
        return this.getType(Clothes.Type.shirt);
    }

    public ArrayList<Clothes> getShoes(){
        return this.getType(Clothes.Type.shoes);
    }

    public ArrayList<Clothes> getPants(){
        return this.getType(Clothes.Type.pants);
    }

    public ArrayList<Clothes> getShorts(){
        return this.getType(Clothes.Type.shorts);
    }

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
