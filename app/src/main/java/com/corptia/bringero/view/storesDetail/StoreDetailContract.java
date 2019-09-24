package com.corptia.bringero.view.storesDetail;

import com.corptia.bringero.base.BaseView;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoreDetailContract {

    public interface StoreDetailView extends BaseView {

        void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail);
        void setProduct(List<GetStoreProductsQuery.Product> product);
        void setProductNotPriced(List<GetNotPricedByQuery.Product> product);

    }

}
