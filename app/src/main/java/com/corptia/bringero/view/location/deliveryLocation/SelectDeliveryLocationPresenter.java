package com.corptia.bringero.view.location.deliveryLocation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.view.Main.login.LoginPresenter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectDeliveryLocationPresenter extends LoginPresenter {

    SelectDeliveryLocationView view;

    public SelectDeliveryLocationPresenter(SelectDeliveryLocationView view) {
        super(view);
        this.view = view;
    }

    public void userUpdateCurrentLocation(String currentDeliveryAddressID) {

        view.showProgress();
        UserInfo userInfo = UserInfo.builder().currentDeliveryAddressID(currentDeliveryAddressID).build();
        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        UpdateUserInfoMutation.@Nullable UpdateInfo updateInfo = response.data().UserMutation().updateInfo();
                        view.hideProgress();
                        if (updateInfo.status() == 200) {
                            getMe(response.data().UserMutation().updateInfo().token(), new onSuccessCall() {
                                @Override
                                public void CallBack(MeQuery.UserData userData) {
                                    Common.CURRENT_USER = userData;
                                    view.onSuccessUpdateCurrentLocation();
                                }
                            });
                        } else {
                            view.onLoginError(updateInfo.message());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.hideProgress();
                        view.onLoginError(e.getMessage());

                    }
                });

    }

    public void addNewAddress(String region  , String addressName){

    }
}
