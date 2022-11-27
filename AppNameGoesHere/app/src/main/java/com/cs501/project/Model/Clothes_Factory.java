package com.cs501.project.Model;/*
 * This class creates Clothes for the backend
 * 
 * Follows the Factory design pattern
 */

import com.cs501.project.Model.Pants;
import com.cs501.project.Model.Shirt;
import com.cs501.project.Model.Shoes;
import com.cs501.project.Model.Shorts;
import com.cs501.project.Model.T_shirt;

public class Clothes_Factory {

    public Clothes_Factory(){
    }

    /*
     * Public Methods
     */
    public T_shirt get_tshirt(){
        return new T_shirt();
    }

    public Shirt get_shirt(){
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

    public Jacket get_jacket(){
        return new Jacket();
    }
}
