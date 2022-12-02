package com.cs501.project.Model;

public class User_settings {

    int oneLayerTemp, threeLayerTemp;

    public User_settings(){

        // default settings
        this.oneLayerTemp = 75;
        this.threeLayerTemp = 45;
    }

    public void setOneLayerTemp(int temp){
        this.oneLayerTemp = temp;
    }
    public void setThreeLayerTemp(int temp){
        this.threeLayerTemp = temp;
    }

    public int getOneLayerTemp(){
        return this.oneLayerTemp;
    }
    public int getThreeLayerTemp(){
        return this.threeLayerTemp;
    }
}
