package com.cs501.project.Model;

public class Shorts extends Clothes {

    public Shorts(){
        super(Type.shorts);
        this.layer = 1;
    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer != 1){
            throw new RuntimeException("Shorts can only be worn on layer 1");
        }
    }
}