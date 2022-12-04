package com.cs501.project.Model;

public class LightJacket extends Clothes{


    public LightJacket() {
        super(Type.LIGHT_JACKET);
        this.layer = 2;

    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer == 1){
            throw new RuntimeException("LightJackets can only be worn on layers 2 or 3");
        }
    }
}
