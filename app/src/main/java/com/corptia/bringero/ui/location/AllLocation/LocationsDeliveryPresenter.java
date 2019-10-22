package com.corptia.bringero.ui.location.AllLocation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.type.DeliveryAddressInput;
import com.corptia.bringero.type.DeliveryAddressSingles;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LocationsDeliveryPresenter extends SelectDeliveryLocationPresenter {

    LocationsDeliveryView view ;


    public LocationsDeliveryPresenter(LocationsDeliveryView view) {
        super(view);
        this.view = view;
    }

    public void removeItems(String currentDeliveryAddressID){

        view.showProgressBar();
        UserInfo userInfo = UserInfo.builder().pULL(DeliveryAddressSingles.builder()
                .deliveryAddresses(DeliveryAddressInput.builder()
                        ._id(currentDeliveryAddressID).build())
                .build()).build();

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        UpdateUserInfoMutation.@Nullable UpdateInfo updateInfo = response.data().UserMutation().updateInfo();
                        view.hideProgressBar();
                        if (updateInfo.status() == 200) {
                            getMe(response.data().UserMutation().updateInfo().token(), new onSuccessCall() {
                                @Override
                                public void CallBack(MeQuery.UserData userData) {
                                    Common.CURRENT_USER = userData;
                                    view.onSuccessRemovedLocation();
                                }
                            });
                        } else {
                            view.showErrorMessage(updateInfo.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgressBar();
                        view.showErrorMessage(e.getMessage());

                    }
                });

    }
}
