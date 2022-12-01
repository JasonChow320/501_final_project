package com.cs501.project.Model;
import java.lang.Math;


public class Color {

    float red;
    float green;
    float blue;
    float red2;
    float green2;
    float blue2;
    String hex1;
    String hex2;

    public Color(){
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.red2 = 0;
        this.green2 = 0;
        this.blue2 = 0;
    }

    public Color(float r, float g, float b, float r2, float g2, float b2, String hex1, String hex2){

        this.hex1 = hex1;
        this.hex2 = hex2;

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

        if(r2 < 0 || r2 >= 256){
            this.red2 = 0;
        } else {
            this.red2 = r2;
        }

        if(g2 < 0 || g2 >= 256){
            this.green2 = 0;
        } else {
            this.green2 = g2;
        }

        if(b2 < 0 || b2 >= 256){
            this.blue2 = 0;
        } else {
            this.blue2 = b2;
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

    public float getRed2(){
        return this.red2;
    }

    public float getGreen2(){
        return this.green2;
    }

    public float getBlue2(){
        return this.blue2;
    }

    public String getHex1(){
        return this.hex1;
    }

    public String getHex2(){
        return this.hex2;
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

        str += " accent=(" + red2 + ", " + green2 + ", " + blue2 + ")";

        return str;
    }
}

