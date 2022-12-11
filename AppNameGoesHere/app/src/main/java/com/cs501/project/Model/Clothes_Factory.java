package com.cs501.project.Model;

/*
 * This class creates Clothes for the backend
 * 
 * Follows the Factory design pattern
 */


public class Clothes_Factory {

    public Clothes_Factory(){
    }

    /*
     * Public Methods
     */
    public T_shirt get_tshirt(){
        return new T_shirt();
    }

    public Shirt get_long_sleeve(){
        return new Shirt();
    }

    public Shoes get_shoes(){
        return new Shoes();
    }

    public Pants get_pants(){
        return new Pants();
    }

    public Shorts get_shorts(){
        return new Shorts();
    }

    public LightJacket get_light_jacket(){
        return new LightJacket();
    }

    public HeavyJacket get_heavyjacket(){
        return new HeavyJacket();
    }

    public Sweater get_sweater(){
        return new Sweater();
    }
}
