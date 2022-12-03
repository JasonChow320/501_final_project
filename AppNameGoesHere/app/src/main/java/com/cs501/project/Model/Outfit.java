package com.cs501.project.Model;

import com.cs501.project.GenerateOutfit;

import java.util.ArrayList;

public class Outfit {

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

    public void addClothesToOutfit(String clothing){

        if(clothing == null){
            return;
        }

        this.clothes_uniqueIds.add(clothing);
    }
}
