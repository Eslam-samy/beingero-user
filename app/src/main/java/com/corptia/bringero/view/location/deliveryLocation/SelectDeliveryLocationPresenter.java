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
import com.corptia.bringero.type.DeliveryAddressesNested;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.type.PointCooridinatesInput;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.type.User_UPDATE_NESTED;
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

    public void addNewAddress(String name, String region, String street, FlatType flatType, int floor, int flat, int building, double lat, double lng, boolean isUpdateCurrentLocation) {

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

                            if (isUpdateCurrentLocation) {
                                String newDeliveryAddressesID = responseUpdateInfo.data().deliveryAddresses().get(responseUpdateInfo.data().deliveryAddresses().size() - 1)._id();
                                userUpdateCurrentLocation(newDeliveryAddressesID);
                            } else {
                                getMe(responseUpdateInfo.token(), userData -> {
                                    view.onSuccessUpdateCurrentLocation();
                                });
                            }

                            //userUpdateCurrentLocation("");


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


    public void updateLocation(String LocationID, String name, String region, String street, FlatType flatType, int floor, int flat, int building, double lat, double lng) {

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

        User_UPDATE_NESTED user_update_nested = User_UPDATE_NESTED.builder()
                .deliveryAddresses(DeliveryAddressesNested.builder()
                        .filter(DeliveryAddressInput.builder()._id(LocationID).build())
                        .data(deliveryAddressInput)
                        .build())
                .build();

        UserInfo userInfo = UserInfo.builder().uPDATE_NESTED(user_update_nested).build();

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        view.hideProgressBar();
                        UpdateUserInfoMutation.UpdateInfo responseUpdateInfo = response.data().UserMutation().updateInfo();

                        if (responseUpdateInfo.status() == 200) {

                            getMe(responseUpdateInfo.token(), userData -> {
                                view.onSuccessUpdateNestedLocation();
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
