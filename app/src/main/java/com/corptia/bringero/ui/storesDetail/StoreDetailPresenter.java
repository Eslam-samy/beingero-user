package com.corptia.bringero.ui.storesDetail;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetNotPricedByQuery;
import com.corptia.bringero.graphql.GetStoreProductsQuery;
import com.corptia.bringero.graphql.SingleStoreHeaderQuery;
import com.corptia.bringero.graphql.SpeedCartQuery;
import com.corptia.bringero.type.PaginationInput;
import com.corptia.bringero.type.ProductFilterInput;
import com.corptia.bringero.type.StoreGalleryFilter;
import com.corptia.bringero.ui.pricing.PricingContract;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.corptia.bringero.utils.recyclerview.PaginationListener.PAGE_SIZE;

public class StoreDetailPresenter {

    StoreDetailContract.StoreDetailView view;

    public StoreDetailPresenter(StoreDetailContract.StoreDetailView view) {
        this.view = view;
    }


    public void getProductStore(String storeId, String typeId, int currentPage) {

            StoreGalleryFilter storeGalleryFilter;
            if (typeId.equals("0")){
              storeGalleryFilter=  StoreGalleryFilter.builder()
                        .discountActive(true)
                        .isAvailable(true).build();
            }else {
            storeGalleryFilter = StoreGalleryFilter.builder()
                .typeId(typeId)
                .isAvailable(true).build();}

        PaginationInput paginationInput = PaginationInput.builder().limit(PAGE_SIZE).page(currentPage).build();

        MyApolloClient.getApollowClientAuthorization().query(GetStoreProductsQuery.builder()
                .pagination(paginationInput)
                .storeId(storeId).filter(storeGalleryFilter).build())
                .enqueue(new ApolloCall.Callback<GetStoreProductsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetStoreProductsQuery.Data> response) {
                        GetStoreProductsQuery.GetStoreProducts getStoreProducts = response.data().PricingProductQuery().getStoreProducts();

                        view.hideProgressBar();
                        Common.adapterIsLoading=false;
                        if (getStoreProducts!=null) {
                            if (getStoreProducts.status() == 200) {
                                view.setProduct(getStoreProducts);
                            } else {
                                view.setPlaceholder();
                            }
                        }else
                            view.setPlaceholder();
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgressBar();
                    }
                });
    }


}
