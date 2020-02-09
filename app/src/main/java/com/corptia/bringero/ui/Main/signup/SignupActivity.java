package com.corptia.bringero.ui.Main.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.PhoneExistsQuery;
import com.corptia.bringero.ui.Main.otp.VerifyPhoneNumberActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SignupActivity extends BaseActivity {

    @BindView(R.id.input_phone_number)
    TextInputLayout input_phone_number;
    @BindView(R.id.input_password)
    TextInputLayout input_password;
    @BindView(R.id.input_confirm_password)
    TextInputLayout input_confirm_password;
    @BindView(R.id.txt_signIn)
    TextView txt_signIn;

    public static String phone, password, firstName, lastName;
    private String confirm_password;
    @BindView(R.id.btn_signup)
    Button btn_signup;

    CustomLoading loading ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loading = new CustomLoading(this , false);
        ButterKnife.bind(this);


        txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = input_phone_number.getEditText().getText().toString().trim();
                password = input_password.getEditText().getText().toString();
                confirm_password = input_confirm_password.getEditText().getText().toString();

                if (validateData()) {


                    loading.showProgressBar(SignupActivity.this , false);

                    checkPhoneExists(phone);


                }

            }
        });

    }

    private void checkPhoneExists(String phone) {

        MyApolloClient.getApollowClient().query(PhoneExistsQuery.builder().phone(phone).build())
                .enqueue(new ApolloCall.Callback<PhoneExistsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<PhoneExistsQuery.Data> response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loading.hideProgressBar();


                                if (response.data().UserQuery().phoneExists()){

                                    Toasty.error(SignupActivity.this ,"This phone number is already registered").show();
//                                    input_phone_number.setErrorEnabled(true);
//                                    input_phone_number.setError("");
                                }
                                else
                                {
                                    Intent intent = new Intent(SignupActivity.this, VerifyPhoneNumberActivity.class);

                                    intent.putExtra(Constants.EXTRA_PASSWORD, password);
                                    intent.putExtra(Constants.EXTRA_PHONE_NUMBER, phone);
                                    intent.putExtra(Constants.EXTRA_SIGNUP, "EXTRA_SIGNUP");

                                    startActivity(intent);

                                    finish();
                                }
                            }
                        });



                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading.hideProgressBar();
                            }
                        });

                    }
                });

    }


    private Boolean validateData() {
        if (password.length() < 8) {
            Toasty.error(this, "Password must be at least \"8\" characters").show();
            return false;
        }
        if (!password.equals(confirm_password)) {
            Toasty.error(this, "Password and confirmation don't match!").show();
            return false;
        }
        if (!phone.startsWith("01")) {
            Toasty.error(this, "Invalid phone! \n It must start with \"01\".").show();
            return false;
        }
        if (phone.length() != 11) {
            Toasty.error(this, "Invalid phone number! \n It must be \"11\" character length.").show();
            return false;
        }

        return true;
    }
}
