package com.corptia.bringero.ui.home.storetypes;

import android.os.Handler;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.GetStoresOfASingleCategoryQuery;
import com.corptia.bringero.graphql.StoreTypesQuery;
import com.corptia.bringero.type.StoreFilterInput;
import com.corptia.bringero.type.StoreStatus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StoreTypesPresenter {

    int offersStores = 0;
    StoreTypesContract.StoreTypesView storeTypesView;
    Handler handler = new Handler();

    public StoreTypesPresenter(StoreTypesContract.StoreTypesView storeTypesView) {
        this.storeTypesView = storeTypesView;
    }

    public void getStoreTypes(String offers) {

        storeTypesView.showProgressBar();

        //Set Data
        //storeTypesList.add(new StoreTypes(R.drawable.img1, "data"));
        StoreFilterInput

            filterInput = StoreFilterInput.builder().isOffer(true).status(StoreStatus.ACTIVE).build();

        MyApolloClient.getApollowClientAuthorization()
                .query(GetStoresOfASingleCategoryQuery.builder()

                        .filter(filterInput)
                        .build())
                .enqueue(new ApolloCall.Callback<GetStoresOfASingleCategoryQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetStoresOfASingleCategoryQuery.Data> offersResponse) {

                        MyApolloClient.getApollowClientAuthorization().query(StoreTypesQuery.builder().build())
                                .enqueue(new ApolloCall.Callback<StoreTypesQuery.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<StoreTypesQuery.Data> response) {

                                        if (offersResponse.data().StoreQuery().getAll().status() == 200) {

                                            offersStores = offersResponse.data().StoreQuery().getAll().pagination().totalDocs();

                                        }
                                        StoreTypesQuery.@Nullable GetAllStoresCount allStores = response.data().StoreTypeQuery().getAllStoresCount();
                                        storeTypesView.hideProgressBar();

                                        if (allStores.status() == 200) {

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    @Nullable List<StoreTypesQuery.StoreCategory> storeCategoryList = new ArrayList<>();
                                                    storeCategoryList.add(new StoreTypesQuery.StoreCategory("Offers" , "" ,
                                                            new StoreTypesQuery.StoreType("","",offers,null),offersStores));
                                                    storeCategoryList.addAll(allStores.StoreCategory());

                                                    Log.d("asasas", "run: " + storeCategoryList.get(1).StoreType().name());
                                                    storeTypesView.setStoreTypes(storeCategoryList);
                                                    storeTypesView.hideProgressBar();
                                                }
                                            });


                                        }

                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {
                                        storeTypesView.hideProgressBar();
                                    }
                                });



                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });



//                .enqueue(new ApolloCall.Callback<GetAllCategoriesQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<GetAllCategoriesQuery.Data> response) {
//
//                        if (response.data().StoreTypeQuery().getAll().status() == 200) {
//
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    List<GetAllCategoriesQuery.StoreCategory> storeCategoryList = response.data().StoreTypeQuery().getAll().StoreCategory();
//                                    storeTypesView.setStoreTypes(storeCategoryList);
//                                    storeTypesView.hideProgressBar();
//                                }
//                            });
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//
//                    }
//                });

        //storeTypesView.setStoreTypes(storeTypesList);
    }

}
