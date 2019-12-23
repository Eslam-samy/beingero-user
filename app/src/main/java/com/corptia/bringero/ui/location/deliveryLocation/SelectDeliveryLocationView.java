package com.corptia.bringero.ui.location.deliveryLocation;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.ui.Main.login.LoginContract;

public interface SelectDeliveryLocationView extends BaseView {

    void onSuccessUpdateCurrentLocation();
    void onSuccessUpdateNestedLocation();

    void onSuccessRemovedLocation();
}
