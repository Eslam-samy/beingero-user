package com.corptia.bringero.ui.Main.otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.SignUpSecondStepMutation;
import com.corptia.bringero.graphql.ValidatePhoneWithFireBaseMutation;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.type.SignupInput;
import com.corptia.bringero.ui.Main.MainActivity;
import com.corptia.bringero.ui.Main.signup.SignupFragment;
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

import org.jetbrains.annotations.NotNull;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        ButterKnife.bind(this);
        txt_lab_message.setText(txt_lab_message.getText() + "\n+2" + SignupFragment.phone);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(true).build();


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
        String number = SignupFragment.phone;
       /* PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS,this,mCallback
        );*/

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + number,        // Phone number to verify
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
                                            .phone(SignupFragment.phone)
                                            .build()).enqueue(new ApolloCall.Callback<ValidatePhoneWithFireBaseMutation.Data>() {
                                        @Override
                                        public void onResponse(@NotNull Response<ValidatePhoneWithFireBaseMutation.Data> response1) {

                                            if (response1.data().UserMutation().validateFireBaseToken().status() == 200) {
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
