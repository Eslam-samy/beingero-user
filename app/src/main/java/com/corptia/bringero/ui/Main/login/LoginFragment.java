package com.corptia.bringero.ui.Main.login;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.splash.SplashActivity;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

import static com.corptia.bringero.Common.Common.isFirstTimeAddLocation;

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
    @BindView(R.id.txt_forgot_password)
    TextView txt_forgot_password;

    LoginPresenter loginPresenter;

    @BindView(R.id.progress_Loading)
    ProgressBar progress_Loading;

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

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }

    private void showBtnHideBar(View viewToShow, View viewToHide) {
        viewToHide.setVisibility(View.GONE);
        ((Button) viewToShow).setText(getString(R.string.login));
    }

    private void showBarHideBtn(View viewToShow, View viewToHide) {
        ((Button) viewToHide).setText("");
        viewToShow.setVisibility(View.VISIBLE);
    }


    @Override
    public void showProgressBar() {
        showBarHideBtn(progress_Loading, btn_login);

    }

    @Override
    public void hideProgressBar() {
        handler.post(() ->
                showBarHideBtn(progress_Loading, btn_login));
    }

    @Override
    public void showErrorMessage(String Message) {

        handler.post(() -> {
            showBtnHideBar(btn_login, progress_Loading);
//            waitingDialog.dismiss();
            Toast.makeText(getActivity(), "" + Message, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onSuccessMessage(String message) {

        handler.post(() -> {

            LocaleHelper.setLocale(getActivity(), Common.CURRENT_USER.getLanguage().toLowerCase());

            PrefUtils.saveToPrefs(getActivity(), PrefKeys.USER_LOGIN, true);
            PrefUtils.saveToPrefs(getActivity(), PrefKeys.USER_PHONE, input_phone_number.getEditText().getText().toString());
            PrefUtils.saveToPrefs(getActivity(), PrefKeys.USER_PASSWORD, input_password.getEditText().getText().toString());

            startActivity(new Intent(getActivity(), SelectDeliveryLocationActivity.class));


            getActivity().finish();
        });
    }

    @Override
    public void onSuccessLoginToMap() {
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        startActivity(new Intent(getActivity(), MapsActivity.class));
                        isFirstTimeAddLocation = true;
                        getActivity().finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        getActivity().finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
}
