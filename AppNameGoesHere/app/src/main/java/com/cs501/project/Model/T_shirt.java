package com.cs501.project.Model;

public class T_shirt extends Clothes {

    public T_shirt() {
        super(Type.T_SHIRT);
        this.layer = 1;

    }

    @Override
    public void setLayer(Integer layer) {
        super.setLayer(layer);

        if (layer != 1){
            throw new RuntimeException("T-shirts can only be worn on layer 1");
        }
    }
}
