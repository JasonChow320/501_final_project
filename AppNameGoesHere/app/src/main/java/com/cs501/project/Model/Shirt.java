package com.cs501.project.Model;

public class Shirt extends Clothes {

    public Shirt() {
        super(Type.LONG_SLEEVE);
        this.layer = 1;
    }

    @Override
    public void setLayer(Integer layer) {
        if (layer == 3){
            throw new RuntimeException("Shirts can only be worn on layers 1 or 2");
        }
    }
}
