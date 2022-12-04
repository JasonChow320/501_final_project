package com.cs501.project.Model;

public class HeavyJacket extends Clothes{


    public HeavyJacket() {
        super(Type.heavy_jacket);
        this.layer = 2;

    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer == 1){
            throw new RuntimeException("Heavy Jackets can only be worn on layers 2 or 3");
        }
    }
}