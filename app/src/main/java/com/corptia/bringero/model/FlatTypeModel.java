package com.corptia.bringero.model;

import com.corptia.bringero.type.FlatType;

public class FlatTypeModel {

    FlatType flatType;

    public FlatTypeModel(FlatType flatType) {
        this.flatType = flatType;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
}
