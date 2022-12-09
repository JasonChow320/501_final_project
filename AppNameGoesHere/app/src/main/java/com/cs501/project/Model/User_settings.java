package com.cs501.project.Model;

public class User_settings {

    int oneLayerTemp, threeLayerTemp, theme, enableCache;
    String flashMode, language;

    public User_settings(){

        // default settings
        this.oneLayerTemp = 75;
        this.threeLayerTemp = 45;
        this.flashMode = "On";
        this.theme = 0;
        this.language = "en";
        this.enableCache = 1;
    }

    public void setOneLayerTemp(int temp){
        this.oneLayerTemp = temp;
    }
    public void setThreeLayerTemp(int temp){
        this.threeLayerTemp = temp;
    }
    public void setFlashMode(String temp) {this.flashMode = temp;}
    public void setTheme(int temp) {this.theme = temp;}
    public void setLanguage(String lang) {this.language = lang;}
    public void setEnableCache(int enable){
        if(enable > 0){
            this.enableCache = 1;
        } else{
            this.enableCache = 0;
        }
    }


    public int getOneLayerTemp(){
        return this.oneLayerTemp;
    }
    public int getThreeLayerTemp(){
        return this.threeLayerTemp;
    }
    public String getFlashMode() { return this.flashMode;}
    public int getTheme() { return this.theme;}
    public String getLanguage() {return this.language; }
    public int getEnableCache() {
        return this.enableCache;
    }
}
