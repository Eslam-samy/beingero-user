package com.corptia.bringero.model;

import java.util.List;

public class CartModel {

    String name;
    List<CartItems> cartItems;

    public CartModel(String name, List<CartItems> cartItems) {
        this.name = name;
        this.cartItems = cartItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CartItems> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItems> cartItems) {
        this.cartItems = cartItems;
    }
}
