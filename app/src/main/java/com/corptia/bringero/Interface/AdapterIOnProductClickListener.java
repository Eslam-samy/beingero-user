package com.corptia.bringero.Interface;

import android.view.View;

import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.StoreSearchQuery;

public interface AdapterIOnProductClickListener {
    void onClickNoSearch(GetStoreProductsQuery.Product product, String cartID, double amount, double price, boolean inCart, boolean isDecrease, View txt_amount, View btn_delete, View bg_delete);

    void onClickSearch(StoreSearchQuery.ProductQuery product, String cartID, double amount, double price, boolean inCart, boolean isDecrease, View txt_amount, View btn_delete, View bg_delete);
}
