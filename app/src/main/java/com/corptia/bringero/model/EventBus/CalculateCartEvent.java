package com.corptia.bringero.model.EventBus;

public class CalculateCartEvent {

    boolean isSuccess;
    double productPrice;
    double amount;


    public CalculateCartEvent(boolean isSuccess, double productPrice , double amount) {
        this.isSuccess = isSuccess;
        this.productPrice = productPrice;
        this.amount = amount;
    }

    public CalculateCartEvent() {
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
