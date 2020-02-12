package com.corptia.bringero.ui.order.orderStoreDetail;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.SingleOrderQuery;

public interface OrderStoreDetailsView extends BaseView {

    void setOrderStoreDetails(SingleOrderQuery.Get storeDetails);

}
