package com.corptia.bringero.ui.home.setting.changePassword;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivityChangePasswordBinding;
import com.corptia.bringero.databinding.ActivityEditProfileBinding;
import com.corptia.bringero.graphql.ChangePasswordMutation;
import com.corptia.bringero.type.ChangePasswordInput;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;

import org.jetbrains.annotations.NotNull;

import es.dmoral.toasty.Toasty;

public class ChangePasswordActivity extends BaseActivity {
    ActivityChangePasswordBinding binding;

    Handler handler;
    CustomLoading customLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        handler = new Handler();


        customLoading = new CustomLoading(this, true);


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePassword();

            }
        });
    }
    private void changePassword() {

        String oldPassword = binding.oldPassword.getText().toString().trim();
        String newPassword = binding.newPassword.getText().toString().trim();
        String newcnofirmPassword = binding.confirmPassword.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || newcnofirmPassword.isEmpty()) {
            Toasty.info(ChangePasswordActivity.this, "Fields are required").show();
        } else if (oldPassword.length() < 8 || newPassword.length() < 8 || newcnofirmPassword.length() < 8) {
            Toasty.info(ChangePasswordActivity.this, "Less than 8 characters").show();
        } else if (!oldPassword.equalsIgnoreCase((String) PrefUtils.getFromPrefs(ChangePasswordActivity.this, PrefKeys.USER_PASSWORD, ""))) {
            Toasty.info(ChangePasswordActivity.this, "Current password not match").show();
        } else if (!newPassword.equals(newcnofirmPassword)) {
            Toasty.info(ChangePasswordActivity.this, "Password does not match").show();
        } else {

            ChangePasswordInput changePasswordInput = ChangePasswordInput.builder()
                    .oldPassword(oldPassword)
                    .newPassword(newPassword)
                    .confirmPassword(newcnofirmPassword).build();

            MyApolloClient.getApollowClientAuthorization().mutate(ChangePasswordMutation.builder().data(changePasswordInput).build()).enqueue(new ApolloCall.Callback<ChangePasswordMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<ChangePasswordMutation.Data> response) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            customLoading.hideProgressBar();

                            if (response.data().UserMutation().changePassword().status() == 200) {


                                PrefUtils.saveToPrefs(ChangePasswordActivity.this, PrefKeys.USER_PASSWORD, newPassword);

                                Toasty.success(ChangePasswordActivity.this, "Password changed successfully").show();

                                Intent intent = new Intent(ChangePasswordActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            } else {

                                if (response.data().UserMutation().changePassword().errors() != null)
                                    Toasty.error(ChangePasswordActivity.this, response.data().UserMutation().changePassword().errors().get(0).message()).show();

                            }

                        }
                    });


                }

                @Override
                public void onFailure(@NotNull ApolloException e) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            customLoading.hideProgressBar();
                            Toasty.error(ChangePasswordActivity.this, "" + e.getMessage());
                        }
                    });
                }
            });

        }

    }
}
