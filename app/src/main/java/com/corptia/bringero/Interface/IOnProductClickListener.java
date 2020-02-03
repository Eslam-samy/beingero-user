package com.corptia.bringero.Interface;

import android.view.View;

public interface IOnProductClickListener {
    void onClick(View view, int position , double price , double amount , boolean isPackaged);

}
