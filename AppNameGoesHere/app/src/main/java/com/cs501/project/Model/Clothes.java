package com.cs501.project.Model;

import java.util.Arrays;
import java.util.stream.Stream;

import java.nio.charset.Charset;
import java.util.Random;

/*
    Clothes contains information about a piece of clothing, all clothes extends this class
 */
public class Clothes {

    public enum Type {

        T_SHIRT,
        LONG_SLEEVE,
        SHORTS,
        PANTS,
        SHOES,
        SWEATER,
        LIGHT_JACKET,
        HEAVY_JACKET
    }

    // public members
    Type type;
    Color color;
    boolean waterResistant;

    private String uniqueId, imageURL;

    Integer layer;

    public Clothes(){
        
        // defaults to T_SHIRT
        this(Type.T_SHIRT);
    }

    public Clothes(Type type){

        // defaults to no color
        this(type, null);
    }

    public Clothes(Type type, Color color){

        // defaults to no color
        this(type, color, "null");
    }

    public Clothes(Type type, Color color, String imageURL){

        // defaults to no imageURL
        this(type, color, imageURL, false);
    }

    public Clothes(Type type, Color color, String imageURL, boolean waterResistant){

        // defaults to no color
        this(type, color, imageURL, waterResistant,null);
    }

    public Clothes(Type type, Color color, String imageURL, boolean waterResistant, Integer layer){

        if(type == null){
           this.type = Type.T_SHIRT; 
        } else {
            this.type = type;
        }

        if(color == null){
            this.color = new Color();
        } else {
            this.color = color;
        }

        if(imageURL == null){
            this.imageURL = new String();
        } else {
            this.imageURL = imageURL;
        }

        this.waterResistant = waterResistant;

        // create unique identifier
        this.uniqueId = RandomString.getAlphaNumericString(16);
        if (layer== null){
            this.layer = 1;
        }
        else {
            this.layer = layer;
        }
    }

    /*
     * Public Methods
     */

    // getters
    public Type getType(){
        return this.type;
    }

    public boolean isWaterResistant(){
        return this.waterResistant;
    }

    public Color getColor(){
        return this.color;
    }

    public String getUniqueId(){
        return this.uniqueId;
    }

    public String getImageURL(){
        return this.imageURL;
    }
    
    public Integer getLayer() {
        return layer;
    }

    // setters
    public void setType(Type type){

        if (type == null) {
            return;
        }
        this.type = type;
    }

    public void setWaterResistant(Boolean isResistant){

        if (isResistant == null) {
            return;
        }
        this.waterResistant = isResistant;
    }

    public void setColor(Color color){

        if (color == null) {
            return;
        }
        this.color = color;
    }

    public void setUniqueId(String id){

        if(id == null){
            return;
        }
        this.uniqueId = id;
    }

    public void setImageURL(String image){

        if(image == null){
            return;
        }
        this.imageURL = image;
    }
    
    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public String toString(){

        String str = new String();
        String bar = "*********************************\n";
        str += bar;
        str += "Type: " + this.type.name() + "\n";
        str += "Water Resistant: " + this.waterResistant + "\n";
        str += "Color: " + this.color.toString() + "\n";
        str += "UniqueId: " + this.uniqueId + "\n";
        str += "imageURL: " + this.imageURL + "\n";
        str += bar;

        return str;
    }

    public static String[] getTypes(Class<? extends Type> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }
}
