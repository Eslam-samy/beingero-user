package com.corptia.bringero.view.Main.login;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.corptia.bringero.R;
import com.corptia.bringero.view.home.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements LoginContract.LoginView {

    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.input_phone_number)
    TextInputLayout input_phone_number;
    @BindView(R.id.input_password)
    TextInputLayout input_password;

    LoginPresenter loginPresenter;

    AlertDialog waitingDialog;

    Handler handler = new Handler();


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        waitingDialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

        loginPresenter = new LoginPresenter(this);

        btn_login.setOnClickListener(view1 -> {

            loginPresenter.onLogin(input_phone_number.getEditText().getText().toString(), input_password.getEditText().getText().toString());
            //HomeActivity .navController .navigate(R.id.action_loginFragment_to_nav_home2);
        });

        return view;
    }

    @Override
    public void showProgress() {

        waitingDialog.show();


    }

    @Override
    public void hideProgress() {
        handler.post(() -> waitingDialog.dismiss());

    }

    @Override
    public void onLoginSuccess(String message) {

        handler.post(() -> {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        });


    }

    @Override
    public void onLoginError(String message) {

        handler.post(() -> {
            waitingDialog.dismiss();
            Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();
        });

    }
}
