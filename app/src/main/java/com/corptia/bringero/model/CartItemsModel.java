package com.corptia.bringero.model;

public class CartItemsModel {

    String cartProductId;
    String pricingProductId;
    double totalPrice;
    int amount;

    public CartItemsModel(String cartProductId, String pricingProductId, double totalPrice, int amount) {
        this.cartProductId = cartProductId;
        this.pricingProductId = pricingProductId;
        this.totalPrice = totalPrice;
        this.amount = amount;
    }

    public CartItemsModel() {
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCartProductId() {
        return cartProductId;
    }

    public void setCartProductId(String cartProductId) {
        this.cartProductId = cartProductId;
    }

    public String getPricingProductId() {
        return pricingProductId;
    }

    public void setPricingProductId(String pricingProductId) {
        this.pricingProductId = pricingProductId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
