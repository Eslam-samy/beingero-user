package com.corptia.bringero.ui.order.main.current;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.DeliveryOrdersQuery;

import java.util.List;

public interface CurrentOrderView extends BaseView {

    void DeliveryOrders(DeliveryOrdersQuery.GetAll deliveryOrderData);
    void CurrentOrders(List<DeliveryOrdersQuery.DeliveryOrderDatum> deliveryOrderData);

    void onNotFoundCurrentOrders();
    void onNotFoundDeliveryOrders();



}
