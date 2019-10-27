package com.corptia.bringero.Adapter;

import com.corptia.bringero.utils.stickyheader.stickyData.StickyMainData;
import com.corptia.bringero.model.CartItems;

import java.util.List;

public class MyData implements StickyMainData {

    String name;
    List<CartItems> cartItems;

    public MyData(String name, List<CartItems> cartItems) {
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
