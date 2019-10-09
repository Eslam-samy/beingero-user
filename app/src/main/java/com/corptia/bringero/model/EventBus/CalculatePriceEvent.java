package com.corptia.bringero.model.EventBus;

public class CalculatePriceEvent {

    String productId;
    int amount;
    double storePrice;
//    int totalPriceItem ;
//    int totalPriceStore ;
//    int totalPriceCart ;

    public CalculatePriceEvent(String productId, int amount, double storePrice) {
        this.productId = productId;
        this.amount = amount;
        this.storePrice = storePrice;

    }


    public CalculatePriceEvent() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getAmount() {
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
