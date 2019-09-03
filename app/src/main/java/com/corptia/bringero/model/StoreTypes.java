package com.corptia.bringero.model;

public class StoreTypes {

    int img;
    String storeTypesName;

    public StoreTypes(int img, String storeTypesName) {
        this.img = img;
        this.storeTypesName = storeTypesName;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getStoreTypesName() {
        return storeTypesName;
    }

    public void setStoreTypesName(String storeTypesName) {
        this.storeTypesName = storeTypesName;
    }
}
