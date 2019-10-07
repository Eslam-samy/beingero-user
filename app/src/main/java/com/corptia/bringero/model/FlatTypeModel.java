package com.corptia.bringero.model;

import com.corptia.bringero.type.FlatType;

import org.intellij.lang.annotations.Language;

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

    //to display object as a string in spinner
    @Override
    public String toString() {
        return flatType.rawValue();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FlatTypeModel){
            FlatTypeModel c = (FlatTypeModel)obj;
            //if(c.getFlatType().rawValue().equals(name) && c.getId()==id ) return true;
        }

        return false;
    }
}
