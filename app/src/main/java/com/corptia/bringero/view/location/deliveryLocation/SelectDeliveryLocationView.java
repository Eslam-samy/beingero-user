package com.corptia.bringero.view.location.deliveryLocation;

import com.corptia.bringero.view.Main.login.LoginContract;

public interface SelectDeliveryLocationView extends LoginContract.LoginView {

    void onSuccessUpdateCurrentLocation();
    void onSuccessUpdateNestedLocation();
}
