package com.corptia.bringero.model;

public class MyCart {

    String productId, cartId;
    Double price, amount, inCartAmount;
    Boolean inCart,isDecrease;

    public MyCart() { }

    public MyCart(String productId, String cartId, Double price, Double amount, Double inCartAmount, Boolean inCart, Boolean isDecrease) {
        this.productId = productId;
        this.cartId = cartId;
        this.price = price;
        this.amount = amount;
        this.inCartAmount = inCartAmount;
        this.inCart = inCart;
        this.isDecrease = isDecrease;
    }

    public Boolean getDecrease() {
        return isDecrease;
    }

    public void setDecrease(Boolean decrease) {
        isDecrease = decrease;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getInCartAmount() {
        return inCartAmount;
    }

    public void setInCartAmount(Double inCartAmount) {
        this.inCartAmount = inCartAmount;
    }

    public Boolean getInCart() {
        return inCart;
    }

    public void setInCart(Boolean inCart) {
        this.inCart = inCart;
    }
}
