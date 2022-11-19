package com.cs501.project.Model;


public class Clothes {

    public enum Type {

        t_shirt,
        shirt,
        shorts,
        pants,
        shoes,
        jacket,
    }

    // public members
    Type type;
    Color color;
    boolean waterResistant;

    public Clothes(){
        
        // defaults to t_shirt
        this(Type.t_shirt);
    }

    public Clothes(Type type){

        // defaults to no color
        this(type, null);
    }

    public Clothes(Type type, Color color){

        // defaults to no color
        this(type, color, false);
    }

    public Clothes(Type type, Color color, boolean waterResistant){

        if(type == null){
           this.type = Type.t_shirt; 
        } else {
            this.type = type;
        }

        if(color == null){
            this.color = new Color();
        } else {
            this.color = color;
        }

        this.waterResistant = waterResistant;
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

    public String toString(){

        String str = new String();
        String bar = "*********************************\n";
        str += bar;
        str += "Type: " + this.type.name() + "\n";
        str += "Water Resistant: " + this.waterResistant + "\n";
        str += "Color: " + this.color.toString() + "\n";
        str += bar;

        return str;
    }
}
