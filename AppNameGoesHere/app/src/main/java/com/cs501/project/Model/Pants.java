package com.cs501.project.Model;

public class Pants extends Clothes {

    public Pants() {
        super(Type.pants);
        this.layer = 1;

    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer != 1){
            throw new RuntimeException("Pants can only be worn on layer 1");
        }
    }
}
