package com.cs501.project.Model;

public class Sweater extends Clothes{

    public Sweater(){
        super(Type.SWEATER);
        this.layer = 2;
    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer != 1){
            throw new RuntimeException("Sweater can only be worn on layer 1");
        }
    }
}
