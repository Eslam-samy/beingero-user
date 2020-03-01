package com.corptia.bringero.ui.Main.resetPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;

import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.LogInMutation;
import com.corptia.bringero.graphql.PhoneExistsQuery;
import com.corptia.bringero.type.LoginInput;
import com.corptia.bringero.ui.Main.otp.VerifyPhoneNumberActivity;
import com.corptia.bringero.ui.Main.signup.SignupActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends BaseActivity {

    @BindView(R.id.input_phone_number)
    TextInputLayout input_phone_number;
    @BindView(R.id.input_new_password)
    TextInputLayout input_new_password;
    @BindView(R.id.input_confirm_password)
    TextInputLayout input_confirm_password;
    @BindView(R.id.btn_next)
    Button btn_next;

    CustomLoading loading ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        loading = new CustomLoading(this , false);
        ButterKnife.bind(this);



        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetPassword();

            }
        });


    }

    private void resetPassword() {

        String phone = input_phone_number.getEditText().getText().toString().trim();
        String newPassword = input_new_password.getEditText().getText().toString().trim();
        String newcnofirmPassword = input_confirm_password.getEditText().getText().toString().trim();

        if (phone.isEmpty() || newPassword.isEmpty() || newcnofirmPassword.isEmpty()){
            Toasty.info(this , "Fields are required").show();
        }

        else if (newPassword.length() < 8 || newcnofirmPassword.length()<8){
            Toasty.info(this , "Less than 8 characters").show();
        }

        else if (!newPassword.equals(newcnofirmPassword)){
            Toasty.info(this , "Password does not match").show();
        }

        else
        {
            //TODO Chk User If User And is Here
//            LoginInput loginInput = LoginInput.builder().phone(phone).password(newPassword)
//            MyApolloClient.getApollowClient().mutate(LogInMutation.builder().loginData())

            checkPhoneExists(phone , newPassword);

        }

    }



    private void checkPhoneExists(String phone , String password) {

        MyApolloClient.getApollowClient().query(PhoneExistsQuery.builder().phone(phone).build())
                .enqueue(new ApolloCall.Callback<PhoneExistsQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<PhoneExistsQuery.Data> response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                loading.hideProgressBar();


                                if (response.data().UserQuery().phoneExists()){

                                    Intent intent = new Intent(ResetPasswordActivity.this , VerifyPhoneNumberActivity.class);
                                    intent.putExtra(Constants.EXTRA_PHONE_NUMBER,phone);
                                    intent.putExtra(Constants.EXTRA_PASSWORD,password);
                                    startActivity(intent);

//                                    input_phone_number.setErrorEnabled(true);
//                                    input_phone_number.setError("");
                                }
                                else
                                {
                                    Toasty.error(ResetPasswordActivity.this ,"This phone number  not registered").show();

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
}
