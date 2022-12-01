package com.cs501.project.Model;

import com.cs501.project.GenerateOutfit;

public class Outfit {
    Clothes[] top;
    Pants bottom;
    Shoes shoes;

    public Outfit(){
        this.top = null;
        this.bottom = null;
        this.shoes = null;
    }






    //Getters


    public Clothes[] getTop() {
        return top;
    }

    public Pants getBottom() {
        return bottom;
    }

    public Shoes getShoes() {
        return shoes;
    }


    //Setters


    public void setTop(Clothes[] top) {
        this.top = top;
    }

    public void setTopItem(int index, Clothes item) {
        this.top[index] = item;
    }

    public void setBottom(Pants bottom) {
        this.bottom = bottom;
    }

    public void setShoes(Shoes shoes) {
        this.shoes = shoes;
    }
}
