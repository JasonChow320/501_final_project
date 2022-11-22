package com.cs501.project.Model;


public abstract class Clothes {

    public enum Type {

        t_shirt,
        shirt,
        shorts,
        pants,
        shoes,
        jacket
    }

    public enum Size {

        small,
        medium,
        large,
        extra_large
    }


    // public members
    Type type;
    Color color;
    Size size;
    Integer layer;

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
        this(type, color, null);
    }

    public Clothes(Type type, Color color, Size size){

        // defaults to no color
        this(type, color, size,null);
    }

    public Clothes(Type type, Color color, Size size, Integer layer){

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

        if(size == null){
            this.size = Size.medium;
        } else {
            this.size = size;
        }

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

    public Size getSize(){
        return this.size;
    }

    public Color getColor(){
        return this.color;
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

    public void setSize(Size size){

        if (size == null) {
            return;
        }
        this.size = size;
    }

    public void setColor(Color color){

        if (color == null) {
            return;
        }
        this.color = color;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public String toString(){

        String str = new String();
        String bar = "*********************************\n";
        str += bar;
        str += "Type: " + this.type.name() + "\n";
        str += "Size: " + this.size.name() + "\n";
        str += "Color: " + this.color.toString() + "\n";
        str += bar;

        return str;
    }
}
