package com.corptia.bringero.ui.storesDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.type.StoreGalleryFilter;
import com.corptia.bringero.ui.pricing.PricingContract;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StoreDetailPresenter {

    StoreDetailContract.StoreDetailView view;
    PricingContract.PricingView pricingView;

    public StoreDetailPresenter(StoreDetailContract.StoreDetailView view) {
        this.view = view;
    }

    public StoreDetailPresenter(PricingContract.PricingView pricingView) {
        this.pricingView = pricingView;
    }

    void getStoreDetailHeader(String storeId) {

        MyApolloClient.getApollowClient().query(SingleStoreHeaderQuery.builder().storeId(storeId).build())
                .enqueue(new ApolloCall.Callback<SingleStoreHeaderQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SingleStoreHeaderQuery.Data> response) {

                        SingleStoreHeaderQuery.Get data = response.data().StoreQuery().get();
                        if (data.status() == 200) {
                            view.setStoresDetailHeader(data.StoreDetail());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }


    public void getProductStore(String storeId, String typeId, boolean isPrice) {

        // ProductFilterInput productFilterInput = null;
        //productFilterInput = ProductFilterInput.builder().typeId(typeId).build();
        //else
        //productFilterInput = ProductFilterInput.builder().typeId(typeId).notPricedBy(Common.CURRENT_STORE._id()).build();
        //  ProductFilterInput productFilterInput = null;
        //if (isPrice)
        //    productFilterInput = ProductFilterInput.builder().typeId(typeId).build();
//        else
//            productFilterInput = ProductFilterInput.builder().typeId(typeId).notPricedBy(Common.CURRENT_STORE._id()).build();

        /*MyApolloClient.getApollowClientAuthorization().query(GetProductQuery.builder().filter(productFilterInput).build())
                .enqueue(new ApolloCall.Callback<GetProductQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetProductQuery.Data> response) {


                        if (response.data().ProductQuery().getAll().status() == 200) {
                            view.setProduct(response.data().ProductQuery().getAll().Product());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });*/

        if (isPrice) {
            StoreGalleryFilter storeGalleryFilter = StoreGalleryFilter.builder().typeId(typeId).isAvailable(true).build();
            MyApolloClient.getApollowClientAuthorization().query(GetStoreProductsQuery.builder().storeId(storeId).filter(storeGalleryFilter).build())
                    .enqueue(new ApolloCall.Callback<GetStoreProductsQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GetStoreProductsQuery.Data> response) {
                            GetStoreProductsQuery.GetStoreProducts getStoreProducts = response.data().PricingProductQuery().getStoreProducts();

                            if (getStoreProducts.status() == 200) {
                                view.setProduct(getStoreProducts.Products());
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });
        } else {

            ProductFilterInput productFilterInput = ProductFilterInput.builder().typeId(typeId).build();
            MyApolloClient.getApollowClientAuthorization().query(GetNotPricedByQuery.builder().storeId(storeId).filter(productFilterInput).build())
                    .enqueue(new ApolloCall.Callback<GetNotPricedByQuery.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<GetNotPricedByQuery.Data> response) {
                            GetNotPricedByQuery.GetStoreProducts getStoreProducts = response.data().ProductQuery().getStoreProducts();
                            if (getStoreProducts.status() == 200) {

                                List<GetNotPricedByQuery.Product> productList = getStoreProducts.Products();

                                view.setProductNotPriced(productList);



                                //view.setProduct();

                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {

                        }
                    });

        }

    }
}
