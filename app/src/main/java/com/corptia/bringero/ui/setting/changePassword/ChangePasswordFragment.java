package com.corptia.bringero.ui.home.setting.changePassword;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.graphql.ChangePasswordMutation;
import com.corptia.bringero.type.ChangePasswordInput;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {

    @BindView(R.id.input_old_password)
    TextInputLayout input_old_password;
    @BindView(R.id.input_new_password)
    TextInputLayout input_new_password;
    @BindView(R.id.input_cnofirm_password)
    TextInputLayout input_cnofirm_password;
    @BindView(R.id.btn_save)
    Button btn_save;

    Handler handler;
    CustomLoading customLoading;

    public ChangePasswordFragment() {

        // Required empty public constructor


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ButterKnife.bind(this, view);
        handler = new Handler();


        customLoading = new CustomLoading(getActivity(), true);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePassword();

            }
        });


        return view;
    }

    private void changePassword() {

        String oldPassword = input_old_password.getEditText().getText().toString().trim();
        String newPassword = input_new_password.getEditText().getText().toString().trim();
        String newcnofirmPassword = input_cnofirm_password.getEditText().getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || newcnofirmPassword.isEmpty()) {
            Toasty.info(getActivity(), "Fields are required").show();
        } else if (oldPassword.length() < 8 || newPassword.length() < 8 || newcnofirmPassword.length() < 8) {
            Toasty.info(getActivity(), "Less than 8 characters").show();
        } else if (!oldPassword.equalsIgnoreCase((String) PrefUtils.getFromPrefs(getActivity(), PrefKeys.USER_PASSWORD, ""))) {
            Toasty.info(getActivity(), "Current password not match").show();
        } else if (!newPassword.equals(newcnofirmPassword)) {
            Toasty.info(getActivity(), "Password does not match").show();
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


                                PrefUtils.saveToPrefs(getActivity(), PrefKeys.USER_PASSWORD, newPassword);

                                Toasty.success(getActivity(), "Password changed successfully").show();

                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            } else {

                                if (response.data().UserMutation().changePassword().errors() != null)
                                    Toasty.error(getActivity(), response.data().UserMutation().changePassword().errors().get(0).message()).show();

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
                            Toasty.error(getActivity(), "" + e.getMessage());
                        }
                    });
                }
            });

        }

    }

}
