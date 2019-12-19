package com.corptia.bringero.model.EventBus;

public class CalculateCartEvent {

    boolean isSuccess;
    double productPrice;


    public CalculateCartEvent(boolean isSuccess, double productPrice) {
        this.isSuccess = isSuccess;
        this.productPrice = productPrice;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }
}
