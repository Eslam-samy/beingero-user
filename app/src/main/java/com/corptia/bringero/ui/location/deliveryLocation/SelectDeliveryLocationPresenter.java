package com.corptia.bringero.ui.location.deliveryLocation;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.DeliveryAddresses;
import com.corptia.bringero.type.DeliveryAddressInput;
import com.corptia.bringero.type.DeliveryAddressSingles;
import com.corptia.bringero.type.DeliveryAddressesNested;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.type.PointCooridinatesInput;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.type.User_UPDATE_NESTED;
import com.corptia.bringero.ui.Main.login.LoginPresenter;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelectDeliveryLocationPresenter {

    SelectDeliveryLocationView view;

    public SelectDeliveryLocationPresenter(SelectDeliveryLocationView view) {
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
                        UpdateUserInfoMutation.CurrentDeliveryAddress currentDeliveryAddress = updateInfo.data().currentDeliveryAddress();

                        view.hideProgressBar();

                        if (updateInfo.status() == 200) {

                            Common.CURRENT_USER.setToken(updateInfo.token());

                            int buildingNumber =0, floorNumber=0 , flatNumber=0 ;

                            if (currentDeliveryAddress.building()!=null){
                                buildingNumber = currentDeliveryAddress.building();
                            }

                            if (currentDeliveryAddress.floor()!=null){
                                floorNumber = currentDeliveryAddress.floor();
                            }

                            if (currentDeliveryAddress.flat()!=null){
                                flatNumber = currentDeliveryAddress.floor();
                            }

                           CurrentDeliveryAddress currentDeliveryAddressModel = Common.getCurrentDeliveryAddress(updateInfo.data().currentDeliveryAddress()._id(),
                                    currentDeliveryAddress.region(),
                                    currentDeliveryAddress.name(),
                                    currentDeliveryAddress.street(),
                                    currentDeliveryAddress.flatType().rawValue(),
                                    new LatLng(currentDeliveryAddress.locationPoint().lat(), currentDeliveryAddress.locationPoint().lng()),
                                    buildingNumber,
                                    floorNumber,
                                    flatNumber
                                    );

//                            CurrentDeliveryAddress currentDeliveryAddressModel = new CurrentDeliveryAddress();
//                            currentDeliveryAddressModel.setId(updateInfo.data().currentDeliveryAddress()._id());
//                            currentDeliveryAddressModel.setBuilding(currentDeliveryAddress.building());
//                            currentDeliveryAddressModel.setFlatType(currentDeliveryAddress.flatType().rawValue());
//                            currentDeliveryAddressModel.setFloor(currentDeliveryAddress.floor());
//                            currentDeliveryAddressModel.setName(currentDeliveryAddress.name());
//                            currentDeliveryAddressModel.setStreet(currentDeliveryAddress.street());
//                            currentDeliveryAddressModel.setRegion(currentDeliveryAddress.region());
//                            currentDeliveryAddressModel.setFlat(currentDeliveryAddress.flat());
//                            currentDeliveryAddressModel.setLocation(new LatLng(currentDeliveryAddress.locationPoint().lat(), currentDeliveryAddress.locationPoint().lng()));


                            Common.CURRENT_USER.setCurrentDeliveryAddress(currentDeliveryAddressModel);

                            view.onSuccessUpdateCurrentLocation();

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


        DeliveryAddressInput deliveryAddressInput;

//        if (floor != 0 && building != 0 && flat != 0) {
            deliveryAddressInput = DeliveryAddressInput.builder()
                    .name(name)
                    .region(region)
                    .street(street)
                    .flatType(flatType)
                    .floor(floor)
                    .flat(flat)
                    .building(building)
                    .locationPoint(inputLocationPoint).build();
//        } else {
//
//            deliveryAddressInput = DeliveryAddressInput.builder()
//                    .name(name)
//                    .region(region)
//                    .street(street)
//                    .flatType(flatType)
//                    .locationPoint(inputLocationPoint).build();
//
//        }


        DeliveryAddressSingles singles = DeliveryAddressSingles.builder().deliveryAddresses(deliveryAddressInput).build();

        UserInfo userInfo = UserInfo.builder().pUSH_SINGLE(singles).build();

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        view.hideProgressBar();
                        UpdateUserInfoMutation.UpdateInfo responseUpdateInfo = response.data().UserMutation().updateInfo();

                        if (responseUpdateInfo.status() == 200) {

                            UpdateUserInfoMutation.DeliveryAddress newDeliveryAddress = responseUpdateInfo.data().deliveryAddresses().get(responseUpdateInfo.data().deliveryAddresses().size() - 1);

                            DeliveryAddresses deliveryAddressesModel = new DeliveryAddresses();
                            deliveryAddressesModel.setId(newDeliveryAddress._id());
                            deliveryAddressesModel.setRegion(newDeliveryAddress.region());
                            deliveryAddressesModel.setName(newDeliveryAddress.name());
                            deliveryAddressesModel.setStreet(newDeliveryAddress.street());
                            deliveryAddressesModel.setFlatType(newDeliveryAddress.flatType().rawValue());

                            if (newDeliveryAddress.building()!=null){
                                deliveryAddressesModel.setFloor(newDeliveryAddress.floor());
                                deliveryAddressesModel.setFlat(newDeliveryAddress.flat());
                                deliveryAddressesModel.setBuilding(newDeliveryAddress.building());
                            }


                            deliveryAddressesModel.setLocation(new LatLng(newDeliveryAddress.locationPoint().lat(), newDeliveryAddress.locationPoint().lng()));

                            Common.CURRENT_USER.getDeliveryAddressesList().add(deliveryAddressesModel);

                            if (isUpdateCurrentLocation || Common.isFirstTimeAddLocation) {

                                String newDeliveryAddressesID = newDeliveryAddress._id();
                                userUpdateCurrentLocation(newDeliveryAddressesID);

                            } else {
                                view.onSuccessUpdateCurrentLocation();
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


    public void updateLocation(int position, String LocationID, String name, String region, String street, FlatType flatType, int floor, int flat, int building, double lat, double lng) {

        view.showProgressBar();

        PointCooridinatesInput inputLocationPoint = PointCooridinatesInput.builder().lat(lat).lng(lng).build();

        DeliveryAddressInput deliveryAddressInput;

//        if (floor != 0 && building != 0 && flat != 0) {
            deliveryAddressInput = DeliveryAddressInput.builder()
                    .name(name)
                    .region(region)
                    .street(street)
                    .flatType(flatType)
                    .floor(floor)
                    .flat(flat)
                    .building(building)
                    .locationPoint(inputLocationPoint).build();
//        } else {
//
//            deliveryAddressInput = DeliveryAddressInput.builder()
//                    .name(name)
//                    .region(region)
//                    .street(street)
//                    .flatType(flatType)
//                    .locationPoint(inputLocationPoint).build();
//
//        }

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

                            DeliveryAddresses deliveryAddresses = new DeliveryAddresses();
                            deliveryAddresses.setId(LocationID);
                            deliveryAddresses.setName(name);
                            deliveryAddresses.setStreet(street);
                            deliveryAddresses.setRegion(region);
                            deliveryAddresses.setFlat(flat);
                            deliveryAddresses.setFlatType(flatType.rawValue());
                            deliveryAddresses.setBuilding(building);
                            deliveryAddresses.setFloor(floor);
                            deliveryAddresses.setLocation(new LatLng(lat , lng));

                            //TODO Miss Here to do reload for all locations
                            Common.CURRENT_USER.getDeliveryAddressesList().set(position ,deliveryAddresses);
                            view.onSuccessUpdateNestedLocation();

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

    public void removeItems(String currentDeliveryAddressID) {

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

                            //TODO Here Miss Refetch and delete old address
                            view.onSuccessRemovedLocation();

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
