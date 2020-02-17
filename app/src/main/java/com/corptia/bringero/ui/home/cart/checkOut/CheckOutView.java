package com.corptia.bringero.ui.home.cart.checkOut;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.ValidateCouponQuery;

import org.jetbrains.annotations.Nullable;

public interface CheckOutView extends BaseView {

    void onSuccessCreateOrder(String orderId , int serial);

    void onSuccessValidateCoupon(ValidateCouponQuery.Data1 couponData);

    void onNotFoundValidateCoupon();

}
