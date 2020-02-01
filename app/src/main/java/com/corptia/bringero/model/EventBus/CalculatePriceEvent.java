package com.corptia.bringero.model.EventBus;

public class CalculatePriceEvent {

    boolean isSuccess;
    String productId;
    double amount;
    double storePrice;
    double totalProductPrice;

//    int totalPriceItem ;
//    int totalPriceStore ;
//    int totalPriceCart ;

    public CalculatePriceEvent(boolean isSuccess,String productId, double amount, double storePrice) {
        this.productId = productId;
        this.amount = amount;
        this.storePrice = storePrice;
        this.isSuccess = isSuccess;

    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public CalculatePriceEvent(double totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public double getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(double totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }

    public CalculatePriceEvent() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getStorePrice() {
        return storePrice;
    }

    public void setStorePrice(double storePrice) {
        this.storePrice = storePrice;
    }
}
