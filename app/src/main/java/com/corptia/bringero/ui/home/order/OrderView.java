package com.corptia.bringero.ui.home.order;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;

import java.util.List;

public interface OrderView extends BaseView {

    void DeliveryOrders(DeliveryOrdersQuery.GetAll deliveryOrderData);
    void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData);

    void onNotFoundCurrentOrders();
    void onNotFoundDeliveryOrders();



}
