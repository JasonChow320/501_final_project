package com.cs501.project.Model;

public class Jacket extends Clothes{


    public Jacket() {
        super(Type.jacket);
        this.layer = 2;

    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer == 1){
            throw new RuntimeException("Jackets can only be worn on layers 2 or 3");
        }
    }
}
