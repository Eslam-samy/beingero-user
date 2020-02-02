package com.corptia.bringero.model;

import com.corptia.bringero.Common.Common;

public class CartItemsModel {

    String cartProductId;
    String pricingProductId;
    double totalPrice;
    double amount;
    boolean isPackaged;

    public CartItemsModel(String cartProductId, String pricingProductId, double totalPrice, double amount, boolean isPackaged) {
        this.cartProductId = cartProductId;
        this.pricingProductId = pricingProductId;
        this.totalPrice = totalPrice;
        this.amount = amount;
        this.isPackaged = isPackaged;

        if (!Common.CART_ITEMS_ID.contains(pricingProductId))
        Common.CART_ITEMS_ID.add(pricingProductId);
    }

    public boolean isPackaged() {
        return isPackaged;
    }

    public void setPackaged(boolean packaged) {
        isPackaged = packaged;
    }

    public CartItemsModel() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
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
