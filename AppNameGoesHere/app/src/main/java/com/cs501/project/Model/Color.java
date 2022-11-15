package com.cs501.project.Model;

public class Color {

    float red;
    float green;
    float blue;

    public Color(){
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public Color(float r, float g, float b){

        if(r < 0 || r >= 256){
            this.red = 0;
        } else {
            this.red = r;
        }

        if(g < 0 || g >= 256){
            this.green = 0;
        } else {
            this.green = g;
        }

        if(b < 0 || b >= 256){
            this.blue = 0;
        } else {
            this.blue = b;
        }
    }

    public String toString(){

        String str = new String();

        str += "(red, green, blue) -> (" + red + ", " + green + ", " + blue + ")";

        return str;
    }
}
