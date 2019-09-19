package com.corptia.bringero.view.storesDetail;

import com.corptia.bringero.graphql.GetProductQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoreDetailContract {

    public interface StoreDetailView {

        void setStoresDetailHeader(SingleStoreHeaderQuery.StoreDetail detail);

        void displayError(String errorMessage);
        void showProgressBar();
        void hideProgressBar();

        void setProduct(List<GetProductQuery.Product> product);
    }

}
