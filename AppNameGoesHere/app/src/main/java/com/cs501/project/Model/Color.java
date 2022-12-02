package com.cs501.project.Model;
import java.lang.Math;
import java.util.ArrayList;

public class Color {

    float red;
    float green;
    float blue;
    float red2;
    float green2;
    float blue2;
    String hex1;
    String hex2;
    ArrayList<Float> hsl1;
    ArrayList<Float> hsl2;

    public Color(){
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.red2 = 0;
        this.green2 = 0;
        this.blue2 = 0;
        this.hex1 = new String("hex");
        this.hex2 = new String("hex2");
        this.hsl1 = new ArrayList<Float>();
        this.hsl2 = new ArrayList<Float>();

        this.hsl1 = rgb2hsl(red, green, blue);
        this.hsl2  = rgb2hsl(red2, green2, blue2);
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

        this.hsl1 = rgb2hsl(r, g, b);
        this.hsl2  = rgb2hsl(r2, g2, b2);
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

    //do not use this to print the HSL val, it will return classtype@hashcode.
    public ArrayList<Float> getHsl1() {
        return hsl1;
    }

    //do not use this to print the HSL val, it will return classtype@hashcode.
    public ArrayList<Float> getHsl2() {
        return hsl2;
    }

    // usefull for detemining if two colors are similar, works for colors of similar shades, even across different hues
    // (if you want to compare different shades of the same color, convert RGB to HSL, and look for colors of similar hue)
    public double ColorDistance(Color e1, Color e2)
    {
        long rmean = ( (long)e1.red + (long)e2.red ) / 2;
        long r = (long)e1.red - (long)e2.red;
        long g = (long)e1.green - (long)e2.green;
        long b = (long)e1.blue - (long)e2.blue;
        return java.lang.Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }

    public ArrayList<Float> rgb2hsl(float r, float g, float b) {
        // Make r, g, and b fractions of 1
        r /= 255;
        g /= 255;
        b /= 255;

        // Find greatest and smallest channel values
        float cmin = Math.min(Math.min(r,g),b),
                cmax = Math.max(Math.max(r,g),b),
                delta = cmax - cmin,
                h = 0,
                s = 0,
                l = 0;

        // Calculate hue
        // No difference
        if (delta == 0)
            h = 0;
            // Red is max
        else if (cmax == r)
            h = ((g - b) / delta) % 6;
            // Green is max
        else if (cmax == g)
            h = (b - r) / delta + 2;
            // Blue is max
        else
            h = (r - g) / delta + 4;

        h = Math.round(h * 60);

        // Make negative hues positive behind 360°
        if (h < 0)
            h += 360;

        // Calculate lightness
        l = (cmax + cmin) / 2;

        // Calculate saturation
        s = delta == 0 ? 0 : delta / (1 - Math.abs(2 * l - 1));

        // Multiply l and s by 100
        s = + Math.round(s * 100);
        l = + Math.round(l * 100);

        ArrayList<Float> ret = new ArrayList<Float>();
        ret.add(h);
        ret.add(s);
        ret.add(l);

        return ret;
    }

    public String toString(){

        String str = new String();

        str += "(red, green, blue) -> (" + red + ", " + green + ", " + blue + ")" + "\n" +
        "(h, s, l) -> (" + hsl1.get(0) + ", " + hsl1.get(1) + ", " + hsl1.get(2) + ")";

        str += " accent=(" + red2 + ", " + green2 + ", " + blue2 + ")" + "\n" +
                "accent=(h, s, l) -> (" + hsl2.get(0) + ", " + hsl2.get(1) + ", " + hsl2.get(2) + ")";

        return str;
    }
}

