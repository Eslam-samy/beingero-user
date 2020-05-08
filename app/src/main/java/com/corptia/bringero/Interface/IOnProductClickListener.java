package com.corptia.bringero.Interface;

import android.view.View;

public interface IOnProductClickListener {
    void onClick(String id, String cartID, double price, double amount, boolean inCart, boolean isDecrease, boolean isPackaged, Double minAmount, View txt_amount, View btn_delete, View bg_delete);
}
