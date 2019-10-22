package com.corptia.bringero.ui.location.deliveryLocation;

import com.corptia.bringero.ui.Main.login.LoginContract;

public interface SelectDeliveryLocationView extends LoginContract.LoginView {

    void onSuccessUpdateCurrentLocation();
    void onSuccessUpdateNestedLocation();
}
