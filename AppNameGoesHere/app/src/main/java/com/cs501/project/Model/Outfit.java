package com.cs501.project.Model;

import com.cs501.project.GenerateOutfit;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/*
    Outfit class stores a user's outfit as an array of the clothes unique ids
 */
public class Outfit implements Comparable<Outfit> {

    private ArrayList<String> clothes_uniqueIds;
    private String outfitUniqueId;
    private String name;

    public Outfit() {
        this.clothes_uniqueIds = new ArrayList<String>();
        this.outfitUniqueId = new String();
        this.name = new String();
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        System.out.println("got name: " + name);
        if(name == null || name.length() <= 0){
            return;
        }
        this.name = name;
    }

    public String getOutfitUniqueId(){
        return this.outfitUniqueId;
    }

    public void setOutfitUniqueId(String id){
        if(id == null || id.length() <= 0){
            return;
        }
        this.outfitUniqueId = id;
    }

    public ArrayList<String> getOutfit(){
        return this.clothes_uniqueIds;
    }

    public void setOutfit(ArrayList<String> outfit){

        if(outfit == null){
            return;
        }
        this.clothes_uniqueIds = outfit;
    }

    public void addClothesToOutfit(String clothingId){

        if(clothingId == null || clothingId.length() <= 0){
            return;
        }

        this.clothes_uniqueIds.add(clothingId);
    }

    @Exclude
    public int getSize(){
        return this.clothes_uniqueIds.size();
    }

    @Override
    public int compareTo(Outfit outfit) {

        int res = -1;
        if(outfit == null || outfit.getSize() != this.clothes_uniqueIds.size()){
            return res;
        }

        res = 0;
        ArrayList<String> outfits = outfit.getOutfit();
        for(String uniqueId : this.clothes_uniqueIds){
            if(!outfits.contains(uniqueId)){
                res = -1;
            }
        }

        return res;
    }
}
