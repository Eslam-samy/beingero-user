package com.corptia.bringero.ui.home.cart.checkOut;

import com.corptia.bringero.base.BaseView;

public interface CheckOutView extends BaseView {

    void onSuccessCreateOrder(String orderId , int serial);
}
