package com.corptia.bringero.ui.Main.login;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.LogInMutation;
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.DeliveryAddresses;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.LoginInput;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.type.UserDeviceInput;
import com.corptia.bringero.type.UserDeviceType;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.corptia.bringero.Common.Common.isFirstTimeGetCartCount;

public class LoginPresenter implements LoginContract.LoginPresenter {

    private LoginContract.LoginView loginView;

    public LoginPresenter(LoginContract.LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void onLogin(String phone, String password) {

        User user = new User(password, phone);
        int isSuccess = user.isValidatieDate();
        if (isSuccess == 0) {
            loginView.showErrorMessage("phone number is Empty");
        } else if (isSuccess == 1)
            loginView.showErrorMessage("phone not matches");

        else if (isSuccess == 2)
            loginView.showErrorMessage("password is too short");

        else {

            loginView.showProgressBar();

            UserDeviceInput userDeviceInput = UserDeviceInput.builder().deviceType(UserDeviceType.ANDROID).token(Common.TOKEN_FIREBASE).build();
            LoginInput loginInput = LoginInput.builder().phone(phone).password(password).device(userDeviceInput).build();

            MyApolloClient.getApollowClient().mutate(LogInMutation.builder().loginData(loginInput).build())
                    .enqueue(new ApolloCall.Callback<LogInMutation.Data>() {
                        @Override
                        public void onResponse(@NotNull Response<LogInMutation.Data> response) {

                            LogInMutation.Login loginData = response.data().UserMutation().login();
                            LogInMutation.Data1 userData = loginData.data();


                            if (loginData.status() == 200)
                            {


                                if (response.data().UserMutation().login().data().roleName().rawValue().equalsIgnoreCase(RoleEnum.CUSTOMER.rawValue()))
                                {

                                    UserModel userModel = new UserModel();

                                    List<DeliveryAddresses> deliveryAddressesList = new ArrayList<>();
                                    for (LogInMutation.DeliveryAddress deliveryAddress :userData.deliveryAddresses())
                                    {
                                        DeliveryAddresses deliveryAddressesModel = new DeliveryAddresses();
                                        deliveryAddressesModel.setId(deliveryAddress._id());
                                        deliveryAddressesModel.setName(deliveryAddress.name());
                                        deliveryAddressesModel.setRegion(deliveryAddress.region());
                                        deliveryAddressesModel.setStreet(deliveryAddress.street());
                                        deliveryAddressesModel.setFlatType(deliveryAddress.flatType().rawValue());
                                        deliveryAddressesModel.setLocation(new LatLng(deliveryAddress.locationPoint().lat() , deliveryAddress.locationPoint().lng()));

                                        if (deliveryAddress.building()!=null){
                                            deliveryAddressesModel.setBuilding(deliveryAddress.building());
                                            deliveryAddressesModel.setFloor(deliveryAddress.floor());
                                            deliveryAddressesModel.setFlat(deliveryAddress.flat());
                                        }


                                        deliveryAddressesList.add(deliveryAddressesModel);
                                    }

                                    userModel.setDeliveryAddressesList(deliveryAddressesList);



                                    userModel.setToken(loginData.token());
                                    userModel.setId(userData._id());
                                    userModel.setAvatarName(userData.AvatarResponse().status()==200 ? userData.AvatarResponse().data().name() :null);
                                    userModel.setEmail(userData.email());
                                    userModel.setFirstName(userData.firstName());
                                    userModel.setFullName(userData.fullName());
                                    userModel.setLanguage(userData.language());
                                    userModel.setLastName(userData.lastName());

                                    userModel.setGender(userData.gender());
                                    userModel.setBirthDate(userData.birthDate());

                                    userModel.setAvatarImageId(userData.avatarImageId());

                                    userModel.setPhone(userData.phone());
                                    userModel.setStatus(userData.status().rawValue());


                                    CurrentDeliveryAddress currentDeliveryAddressModel = null ;

                                    if (userData.currentDeliveryAddress()!=null) {


                                        int buildingNumber =0, floorNumber=0 , flatNumber=0 ;

                                        if (userData.currentDeliveryAddress().building()!=null){
                                            buildingNumber = userData.currentDeliveryAddress().building();
                                        }

                                        if (userData.currentDeliveryAddress().floor()!=null){
                                            floorNumber = userData.currentDeliveryAddress().floor();
                                        }

                                        if (userData.currentDeliveryAddress().flat()!=null){
                                            flatNumber = userData.currentDeliveryAddress().floor();
                                        }

                                        currentDeliveryAddressModel = Common.getCurrentDeliveryAddress(userData.currentDeliveryAddress()._id(),
                                                userData.currentDeliveryAddress().region(),
                                                userData.currentDeliveryAddress().name(),
                                                userData.currentDeliveryAddress().street(),
                                                userData.currentDeliveryAddress().flatType().rawValue(),
                                                new LatLng(userData.currentDeliveryAddress().locationPoint().lat(), userData.currentDeliveryAddress().locationPoint().lng()),
                                                buildingNumber,
                                                floorNumber,
                                                flatNumber
                                        );


                                    }

                                    userModel.setCurrentDeliveryAddress(currentDeliveryAddressModel);


                                    Common.CURRENT_USER= userModel;

                                    if (userData.currentDeliveryAddress()!=null)
                                        loginView.onSuccessMessage("");
                                    else
                                        loginView.onSuccessLoginToMap();
                                }
                                else
                                {

                                    loginView.hideProgressBar();
                                    loginView.onErrorRole(response.data().UserMutation().login().data().roleName().rawValue());
                                }



                            }
                            else
                            {
                                loginView.hideProgressBar();
                                loginView.showErrorMessage("" + response.data().UserMutation().login().message());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull ApolloException e) {
                            loginView.hideProgressBar();
                            loginView.showErrorMessage("[LOG IN]"+e.getMessage());

                        }
                    });
        }

    }

}
