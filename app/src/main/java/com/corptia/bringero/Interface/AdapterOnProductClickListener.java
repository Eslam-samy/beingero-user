package com.corptia.bringero.Interface;

import android.view.View;
import android.widget.TextView;

import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.StoreSearchQuery;

public interface AdapterOnProductClickListener {
    void onClickSearch(StoreSearchQuery.ProductQuery product, String cartID, double price, double amount, boolean inCart, boolean isDecrease, TextView txt_amount, View btn_delete, View bg_delete);

    void onClickNoSearch(GetStoreProductsQuery.Product searchProduct, String cartID, double price, double amount, boolean inCart, boolean isDecrease, TextView txt_amount, View btn_delete, View bg_delete);
}
