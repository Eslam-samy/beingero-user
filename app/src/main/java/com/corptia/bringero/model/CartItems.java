package com.corptia.bringero.model;

public class CartItems {

    String productName;

    public CartItems(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
