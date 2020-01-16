package com.corptia.bringero.ui.Main.otp;

import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.chaos.view.PinView;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.ResetPasswordMutation;
import com.corptia.bringero.graphql.SignUpSecondStepMutation;
import com.corptia.bringero.graphql.ValidatePhoneWithFireBaseMutation;
import com.corptia.bringero.model.CurrentDeliveryAddress;
import com.corptia.bringero.model.DeliveryAddresses;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.ResetPasswordInput;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.type.SignupInput;
import com.corptia.bringero.ui.Main.MainActivity;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.uzairiqbal.circulartimerview.CircularTimerListener;
import com.uzairiqbal.circulartimerview.CircularTimerView;
import com.uzairiqbal.circulartimerview.TimeFormatEnum;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class VerifyPhoneNumberActivity extends BaseActivity {

    private static final String TAG = "TryCustomToken";
    @BindView(R.id.pinView)
    PinView pinView;
    @BindView(R.id.btn_verify)
    Button btn_verify;
    @BindView(R.id.txt_lab_message)
    TextView txt_lab_message;

    AlertDialog dialog;

    FirebaseAuth auth;
    String verification_code;


    String phone, password;

    @BindView(R.id.countDown)
    CircularTimerView countDown;
    @BindView(R.id.btn_resend)
    Button btn_resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        ButterKnife.bind(this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(true).build();


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_PASSWORD)) {
            phone = intent.getStringExtra(Constants.EXTRA_PHONE_NUMBER);
            password = intent.getStringExtra(Constants.EXTRA_PASSWORD);
        } else {
            phone = SignupFragment.phone;
        }
        txt_lab_message.setText(txt_lab_message.getText() + "\n+2" + phone);

        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("ar");

        sent();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                verified();
            }
        });


        // To Initialize Timer
        countDown.setCircularTimerListener(new CircularTimerListener() {
            @Override
            public String updateDataOnTick(long remainingTimeInMs) {
                return String.valueOf((int) Math.ceil((remainingTimeInMs / 1000.f)));
            }

            @Override
            public void onTimerFinished() {
                countDown.setPrefix("");
                countDown.setSuffix("");
//                countDown.setText("FINISHED THANKS!");
                countDown.setVisibility(View.INVISIBLE);
                btn_resend.setVisibility(View.VISIBLE);

            }
        }, 60, TimeFormatEnum.SECONDS, 10);


// To start timer

        countDown.setProgress(0);
        countDown.startTimer();


        btn_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countDown.setProgress(0);
                countDown.startTimer();
                countDown.setVisibility(View.VISIBLE);
                btn_resend.setVisibility(View.INVISIBLE);

                sent();
            }
        });

    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("onVerificationCompleted", phoneAuthCredential.getProvider());

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.d("onVerificationFailed", e.getMessage());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verification_code = s;
            Log.d("onCodeSent", "Code sent to the number");
        }
    };


    public void sent() {
       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mCallback
        );*/

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallback);        // OnVerificationStateChangedCallbacks

    }


    public void signInWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]

                            user.getIdToken(true).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                }
                            }).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                @Override
                                public void onSuccess(GetTokenResult getTokenResult) {

                                    MyApolloClient.getApollowClient().mutate(ValidatePhoneWithFireBaseMutation
                                            .builder()
                                            .fireBaseToken(getTokenResult.getToken())
                                            .phone(phone)
                                            .build()).enqueue(new ApolloCall.Callback<ValidatePhoneWithFireBaseMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<ValidatePhoneWithFireBaseMutation.Data> response1) {

                                            if (response1.data().UserMutation().validateFireBaseToken().status() == 200) {

                                                String newToken = response1.data()
                                                        .UserMutation().validateFireBaseToken().token();
                                                //For Rest
                                                if (password != null && !password.isEmpty()) {


                                                    ResetPasswordInput resetPasswordInput = ResetPasswordInput.builder().phone(phone)
                                                            .confirmPassword(password)
                                                            .newPassword(password).build();

                                                    MyApolloClient.getApollowClientAuthorization(newToken)
                                                            .mutate(ResetPasswordMutation.builder().data(resetPasswordInput).build()).enqueue(new ApolloCall.Callback<ResetPasswordMutation.Data>() {
                                                        @Override
                                                        public void onResponse(@NotNull Response<ResetPasswordMutation.Data> response) {

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
                                                                if (userResponse.currentDeliveryAddress()!=null) {
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

                                                                Common.CURRENT_USER= userModel;

                                                                Common.GetCartItemsCount();

                                                                Toasty.success(VerifyPhoneNumberActivity.this  , "تم تغير كلمة السر بنجاح").show();
                                                                Intent intent = new Intent(VerifyPhoneNumberActivity.this , MainActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                //getActivity().finishAffinity();
                                                                startActivity(intent);
                                                                finish();

                                                            } else {

                                                            }


                                                        }

                                                        @Override
                                                        public void onFailure(@NotNull ApolloException e) {


                                                        }
                                                    });


                                                } else {
                                                    SignupInput signupInput = SignupInput.builder()
                                                            .firstName(SignupFragment.firstName)
                                                            .lastName(SignupFragment.lastName)
                                                            .password(SignupFragment.password)
                                                            .phone(SignupFragment.phone).roleName(RoleEnum.CUSTOMER).build();
                                                    MyApolloClient.getApollowClientAuthorization(
                                                            response1.data().UserMutation().validateFireBaseToken().token()
                                                    ).mutate(SignUpSecondStepMutation.builder().data(signupInput).build())
                                                            .enqueue(new ApolloCall.Callback<SignUpSecondStepMutation.Data>() {
                                                                @Override
                                                                public void onResponse(@NotNull Response<SignUpSecondStepMutation.Data> response) {

                                                                    if (response.data().UserMutation().signup().status() == 200) {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Toast.makeText(VerifyPhoneNumberActivity.this, "Welcome " + SignupFragment.firstName
                                                                                                + " "
                                                                                                + SignupFragment.lastName
                                                                                        , Toast.LENGTH_SHORT).show();
                                                                                dialog.dismiss();

                                                                            }
                                                                        });
                                                                        startActivity(new Intent(VerifyPhoneNumberActivity.this, MainActivity.class));
                                                                        finish();
                                                                        SignupFragment.firstName = null;
                                                                        SignupFragment.lastName = null;
                                                                        SignupFragment.password = null;
                                                                    } else {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialog.dismiss();
                                                                                Toasty.error(VerifyPhoneNumberActivity.this, response1.data()
                                                                                        .UserMutation()
                                                                                        .validateFireBaseToken()
                                                                                        .message())
                                                                                        .show();

                                                                            }
                                                                        });
                                                                        startActivity(new Intent(VerifyPhoneNumberActivity.this, MainActivity.class));
                                                                        finish();
                                                                        SignupFragment.firstName = null;
                                                                        SignupFragment.lastName = null;
                                                                        SignupFragment.password = null;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(@NotNull ApolloException e) {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            dialog.dismiss();
                                                                            Toasty.error(VerifyPhoneNumberActivity.this, "Failed to sign up!").show();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                }


                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();
                                                        Toasty.error(VerifyPhoneNumberActivity.this, response1.data()
                                                                .UserMutation()
                                                                .validateFireBaseToken()
                                                                .message())
                                                                .show();
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull ApolloException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    Toasty.error(VerifyPhoneNumberActivity.this, "Failed to sign up!").show();
                                                }
                                            });
                                        }
                                    });


                                }
                            });
                            //String tokenCustom = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImlhdCI6MTU2NzYxMDc3MCwiZXhwIjoxNTY3NjE0MzcwLCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay15bHhzdUB0ZXN0LXNpZ24taW4tMWNjODcuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay15bHhzdUB0ZXN0LXNpZ24taW4tMWNjODcuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiJtYUlyV0NkSlJaU1NIVDlMbDN5d3JvemoybmcyIn0.HqM3lk5ci3yCOxLzJnVzwtTET0vkz55sNTLB8qyEM5Ew8X_p_tPTbr41CneZ3_pl2d1rsXsdgas6Q1kYi9n9rGy1U3SnB4AHJ_CyjITgEvxW7ZAV7fE9jfU0QVkdgZ3VlkHBgve8IEp0q5cYY5WMEgTTqHgPJSgmX5vHcpbW-Bmd2hCQ2wDuczzzr9mKS-6zYvdVEHGp_J37aweE53CKQhcqngrof5Qc8sX7z8DFlQfbugEEGm8C9Ffaw-_zF3akMZ3fAm75klFm1G9-bfBieYTThDMzMsJ88tfpGXP-E_Va4PK3-NaxNaGV6WxwJYqOvMbvAYYUWzQyRNV_0-jH9g";

                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VerifyPhoneNumberActivity.this, "Invalid code.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI

                            // [END_EXCLUDE]
                        }
                    }
                });

    }

    public void verified() {
        if (!pinView.getText().toString().equalsIgnoreCase("") || !pinView.getText().toString().isEmpty()) {
            String input_code = pinView.getText().toString();
            if (verification_code != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, input_code);
                signInWithPhone(credential);
            }
        } else
            dialog.dismiss();
    }
}
