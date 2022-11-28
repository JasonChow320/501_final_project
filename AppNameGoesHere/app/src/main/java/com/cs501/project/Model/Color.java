package com.cs501.project.Model;
import java.lang.Math;


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

    // public methods
    public float getRed(){
        return this.red;
    }

    public float getGreen(){
        return this.green;
    }

    public float getBlue(){
        return this.blue;
    }

    // usefull for detemining if two colors are similar, works for colors of similar shades, even across different hues
    // (if you want to compare different shades of the same color, convert RGB to HSL, and look for colors of similar hue)
    public static double ColorDistance(Color e1, Color e2)
    {
        long rmean = ( (long)e1.red + (long)e2.red ) / 2;
        long r = (long)e1.red - (long)e2.red;
        long g = (long)e1.green - (long)e2.green;
        long b = (long)e1.blue - (long)e2.blue;
        return java.lang.Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }

    public String toString(){

        String str = new String();

        str += "(red, green, blue) -> (" + red + ", " + green + ", " + blue + ")";

        return str;
    }
}

