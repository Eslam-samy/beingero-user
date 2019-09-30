package com.corptia.bringero.base;

import com.corptia.bringero.graphql.DeliveryOrdersQuery;

import java.util.List;

public interface BaseView {

    void showProgressBar();
    void hideProgressBar();
    void showErrorMessage(String Message);


}
