package com.cs501.project.Model;

import com.cs501.project.GenerateOutfit;

import java.util.ArrayList;

public class Outfit {

    private ArrayList<String> clothes_uniqueIds;

    public Outfit() {
        this.clothes_uniqueIds = new ArrayList<String>();
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
