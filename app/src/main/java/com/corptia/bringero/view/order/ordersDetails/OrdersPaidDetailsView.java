package com.corptia.bringero.view.order.ordersDetails;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.DeliveryOneOrderQuery;
import com.corptia.bringero.view.order.main.current.CurrentOrderView;

import org.jetbrains.annotations.Nullable;

public interface OrdersPaidDetailsView extends BaseView {

    void setSingleOrder(DeliveryOneOrderQuery.@Nullable DeliveryOrderData deliveryOrderData);

}
