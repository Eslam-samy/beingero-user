package com.corptia.bringero.ui.Main.resetPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.ResetPasswordMutation;
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.DeliveryAddresses;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.ResetPasswordInput;
import com.corptia.bringero.ui.Main.login.LoginActivity;
import com.corptia.bringero.ui.Main.otp.VerifyPhoneNumberActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ResetPasswordStepTwo extends AppCompatActivity {
    @BindView(R.id.input_new_password)
    TextInputLayout input_new_password;
    @BindView(R.id.input_confirm_password)
    TextInputLayout input_confirm_password;
    @BindView(R.id.btn_next)
    Button btn_next;

    CustomLoading loading;
    private String phone;
    private String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_step_two);

        loading = new CustomLoading(this, false);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(Constants.EXTRA_PHONE_NUMBER)) {
            phone = getIntent().getStringExtra(Constants.EXTRA_PHONE_NUMBER);
        }

        if (getIntent().hasExtra(Constants.EXTRA_NEW_TOKEN)) {
            newToken = getIntent().getStringExtra(Constants.EXTRA_NEW_TOKEN);
        }


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetPassword();

            }
        });
    }

    private void resetPassword() {

        String newPassword = input_new_password.getEditText().getText().toString().trim();
        String newcnofirmPassword = input_confirm_password.getEditText().getText().toString().trim();

        if (newPassword.isEmpty() || newcnofirmPassword.isEmpty()) {
            Toasty.info(this, "Fields are required").show();
        } else if (newPassword.length() < 8 || newcnofirmPassword.length() < 8) {
            Toasty.info(this, "Less than 8 characters").show();
        } else if (!newPassword.equals(newcnofirmPassword)) {
            Toasty.info(this, "Password does not match").show();
        } else {
            //TODO Chk User If User And is Here
//            LoginInput loginInput = LoginInput.builder().phone(phone).password(newPassword)
//            MyApolloClient.getApollowClient().mutate(LogInMutation.builder().loginData())

            completePasswordReset(newPassword);

        }

    }

    private void completePasswordReset(String password) {
        ResetPasswordInput resetPasswordInput = ResetPasswordInput.builder().phone(phone)
                .confirmPassword(password)
                .newPassword(password).build();

        MyApolloClient.getApollowClientAuthorization(newToken)
                .mutate(ResetPasswordMutation.builder().data(resetPasswordInput).build()).enqueue(new ApolloCall.Callback<ResetPasswordMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<ResetPasswordMutation.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.data().UserMutation().resetPassword().status() == 200) {

                            ResetPasswordMutation.Data1 userResponse = response.data().UserMutation().resetPassword().data();
                            UserModel userModel = new UserModel();


                            List<DeliveryAddresses> deliveryAddressesList = new ArrayList<>();
                            for (ResetPasswordMutation.DeliveryAddress deliveryAddress : userResponse.deliveryAddresses()) {
                                DeliveryAddresses deliveryAddressesModel = new DeliveryAddresses();
                                deliveryAddressesModel.setId(deliveryAddress._id());
                                deliveryAddressesModel.setName(deliveryAddress.name());
                                deliveryAddressesModel.setRegion(deliveryAddress.region());
                                deliveryAddressesModel.setStreet(deliveryAddress.street());
                                deliveryAddressesModel.setBuilding(deliveryAddress.building());
                                deliveryAddressesModel.setFlatType(deliveryAddress.flatType().rawValue());
                                deliveryAddressesModel.setFloor(deliveryAddress.floor());
                                deliveryAddressesModel.setFlat(deliveryAddress.flat());
                                deliveryAddressesModel.setLocation(new LatLng(deliveryAddress.locationPoint().lat(), deliveryAddress.locationPoint().lng()));


                                deliveryAddressesList.add(deliveryAddressesModel);
                            }

                            userModel.setDeliveryAddressesList(deliveryAddressesList);


                            userModel.setToken(newToken);
                            userModel.setBirthDate(userResponse.birthDate());
                            userModel.setGender(userResponse.gender());
                            userModel.setAvatarName(userResponse.AvatarResponse().status() == 200 ?
                                    userResponse.AvatarResponse().data().name() : null);
                            userModel.setAvatarImageId(userResponse.avatarImageId());
                            userModel.setLastName(userResponse.lastName());
                            userModel.setFirstName(userResponse.firstName());
                            userModel.setFullName(userResponse.fullName());
                            userModel.setPhone(userResponse.phone());
                            userModel.setLanguage(userResponse.language());


                            CurrentDeliveryAddress currentDeliveryAddressModel = new CurrentDeliveryAddress();
                            if (userResponse.currentDeliveryAddress() != null) {
                                currentDeliveryAddressModel.setId(userResponse.currentDeliveryAddress()._id());
                                currentDeliveryAddressModel.setBuilding(userResponse.currentDeliveryAddress().building());
                                currentDeliveryAddressModel.setFlatType(userResponse.currentDeliveryAddress().flatType().rawValue());
                                currentDeliveryAddressModel.setStreet(userResponse.currentDeliveryAddress().street());
                                currentDeliveryAddressModel.setName(userResponse.currentDeliveryAddress().name());
                                currentDeliveryAddressModel.setRegion(userResponse.currentDeliveryAddress().region());
                                currentDeliveryAddressModel.setFloor(userResponse.currentDeliveryAddress().floor());
                                currentDeliveryAddressModel.setFlat(userResponse.currentDeliveryAddress().flat());
                                currentDeliveryAddressModel.setLocation(new LatLng(userResponse.currentDeliveryAddress().locationPoint().lat(), userResponse.currentDeliveryAddress().locationPoint().lng()));
                            }
                            userModel.setCurrentDeliveryAddress(currentDeliveryAddressModel);

                            Common.CURRENT_USER = userModel;

                            Common.GetCartItemsCount(null);

                            Toasty.success(ResetPasswordStepTwo.this, "تم تغير كلمة السر بنجاح").show();
                            Intent intent = new Intent(ResetPasswordStepTwo.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            //getActivity().finishAffinity();
                            startActivity(intent);
                            finish();

                        }
                    }
                });


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {


            }
        });

    }

}
