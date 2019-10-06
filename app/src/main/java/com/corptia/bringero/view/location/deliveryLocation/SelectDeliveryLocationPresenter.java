package com.corptia.bringero.view.location.deliveryLocation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.type.DeliveryAddressInput;
import com.corptia.bringero.type.DeliveryAddressSingles;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.type.PointCooridinatesInput;
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

        view.showProgressBar();
        UserInfo userInfo = UserInfo.builder().currentDeliveryAddressID(currentDeliveryAddressID).build();
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
                                    view.onSuccessUpdateCurrentLocation();
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

    public void addNewAddress(String name, String region, String street, FlatType flatType, int floor, int flat, int building, double lat, double lng) {

        view.showProgressBar();


        PointCooridinatesInput inputLocationPoint = PointCooridinatesInput.builder().lat(lat).lng(lng).build();

        DeliveryAddressInput deliveryAddressInput = DeliveryAddressInput.builder()
                .name(name)
                .region(region)
                .street(street)
                .flatType(flatType)
                .floor(floor)
                .flat(flat)
                .building(building)
                .locationPoint(inputLocationPoint).build();

        DeliveryAddressSingles singles = DeliveryAddressSingles.builder().deliveryAddresses(deliveryAddressInput).build();

        UserInfo userInfo = UserInfo.builder().pUSH_SINGLE(singles).build();

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        view.hideProgressBar();
                        UpdateUserInfoMutation.UpdateInfo responseUpdateInfo = response.data().UserMutation().updateInfo();
                        if (responseUpdateInfo.status() == 200) {

                            //userUpdateCurrentLocation("");

                            getMe(responseUpdateInfo.token(), userData -> {
                                view.onSuccessUpdateCurrentLocation();
                            });
                        } else {
                            view.showErrorMessage(responseUpdateInfo.message());
                        }

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        view.showErrorMessage(e.getMessage());
                        view.hideProgressBar();


                    }
                });

    }
}
