package com.corptia.bringero.ui.order.ordersDetails;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;

import org.jetbrains.annotations.Nullable;

public interface OrdersPaidDetailsView extends BaseView {

    void setSingleOrder(DeliveryOneOrderQuery.@Nullable DeliveryOrderData deliveryOrderData);

}
