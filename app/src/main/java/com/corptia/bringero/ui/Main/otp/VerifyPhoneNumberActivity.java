package com.corptia.bringero.ui.Main.otp;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.corptia.bringero.ui.Main.login.LoginActivity;
import com.corptia.bringero.ui.Main.resetPassword.ResetPasswordStepTwo;
import com.corptia.bringero.ui.Main.signup.SignupActivity;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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
    private static int time = 60;

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
    @BindView(R.id.resend)
    Button resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        ButterKnife.bind(this);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(true).build();


        Intent intent = getIntent();

        if (intent != null) {
            phone = intent.getStringExtra(Constants.EXTRA_PHONE_NUMBER);
            password = intent.getStringExtra(Constants.EXTRA_PASSWORD);
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
                                                if (getIntent().hasExtra(Constants.EXTRA_SIGNUP)) {

                                                    runOnUiThread(() -> {


                                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(VerifyPhoneNumberActivity.this);

                                                        View view = LayoutInflater.from(VerifyPhoneNumberActivity.this).inflate(R.layout.layout_signup, null);

                                                        TextInputLayout input_firstName = view.findViewById(R.id.input_firstName);
                                                        TextInputLayout input_lastName = view.findViewById(R.id.input_lastName);

                                                        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                                SignupInput signupInput = SignupInput.builder()
                                                                        .password(SignupActivity.password)
                                                                        .phone(SignupActivity.phone)
                                                                        .firstName(input_firstName.getEditText().getText().toString())
                                                                        .lastName(input_lastName.getEditText().getText().toString())
                                                                        .roleName(RoleEnum.CUSTOMER).build();

                                                                MyApolloClient.getApollowClientAuthorization(
                                                                        response1.data()
                                                                                .UserMutation()
                                                                                .validateFireBaseToken()
                                                                                .token())
                                                                        .mutate(SignUpSecondStepMutation.builder().data(signupInput).build())
                                                                        .enqueue(new ApolloCall.Callback<SignUpSecondStepMutation.Data>() {
                                                                            @Override
                                                                            public void onResponse(@NotNull Response<SignUpSecondStepMutation.Data> response) {

                                                                                if (response.data().UserMutation().signup().status() == 200) {
                                                                                    runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
//                                                                                Toast.makeText(VerifyPhoneNumberActivity.this, "Welcome " + SignupFragment.firstName
//                                                                                                + " "
//                                                                                                + SignupFragment.lastName
//                                                                                        , Toast.LENGTH_SHORT).show();
                                                                                            dialog.dismiss();

                                                                                            startActivity(new Intent(VerifyPhoneNumberActivity.this, LoginActivity.class));
                                                                                            finish();
                                                                                            SignupActivity.firstName = null;
                                                                                            SignupActivity.lastName = null;
                                                                                            SignupActivity.password = null;

                                                                                        }
                                                                                    });

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
                                                                                    startActivity(new Intent(VerifyPhoneNumberActivity.this, LoginActivity.class));
                                                                                    finish();
                                                                                    SignupActivity.firstName = null;
                                                                                    SignupActivity.lastName = null;
                                                                                    SignupActivity.password = null;
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

                                                        builder.setView(view);
                                                        androidx.appcompat.app.AlertDialog dialog = builder.create();
                                                        dialog.setCancelable(false);
                                                        dialog.show();

                                                    });

                                                } else {
                                                    Intent intent = new Intent(VerifyPhoneNumberActivity.this, ResetPasswordStepTwo.class);
                                                    intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phone);
                                                    intent.putExtra(Constants.EXTRA_NEW_TOKEN, newToken);
                                                    startActivity(intent);
                                                    finish();
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

    @SuppressLint("SetTextI18n")
    private void startTimer() {
        time = 60;
        resend.setEnabled(false);
        resend.setText(getString(R.string.send_new_code) + " (0:" + checkDigit(time) + ")");
        new CountDownTimer(61000, 1000) {

            public void onTick(long millisUntilFinished) {
                try {
                    resend.setText(getString(R.string.send_new_code) + " (0:" + checkDigit(time) + ")");
                    time--;
                } catch (Exception ignored) {
                }
            }

            public void onFinish() {
                try {
                    resend.setText(getString(R.string.resend_code));
                    resend.setEnabled(true);
                } catch (Exception ignored) {
                }
            }

        }.start();
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
