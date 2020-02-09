package com.corptia.bringero.ui.Main.login;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.ui.Main.resetPassword.ResetPasswordActivity;
import com.corptia.bringero.ui.Main.signup.SignupActivity;
import com.corptia.bringero.ui.Main.suspend.SuspendActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.corptia.bringero.utils.button.ProgressButton;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
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
import es.dmoral.toasty.Toasty;

import static com.corptia.bringero.Common.Common.isFirstTimeAddLocation;

public class LoginActivity extends BaseActivity implements LoginContract.LoginView {

    @BindView(R.id.btn_login)
    View btn_login;
    @BindView(R.id.input_phone_number)
    TextInputLayout input_phone_number;
    @BindView(R.id.input_password)
    TextInputLayout input_password;
    @BindView(R.id.txt_forgot_password)
    TextView txt_forgot_password;
    @BindView(R.id.txt_signUp)
    TextView txt_signUp;

    LoginPresenter loginPresenter;

    AlertDialog waitingDialog;

    //This Custom Button Loading
    ProgressButton progressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        progressButton = new ProgressButton(this, btn_login);
        waitingDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        loginPresenter = new LoginPresenter(this);

        btn_login.setOnClickListener(view1 -> {

            loginPresenter.onLogin(input_phone_number.getEditText().getText().toString(), input_password.getEditText().getText().toString());
            //HomeActivity .navController .navigate(R.id.action_loginFragment_to_nav_home2);
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this , ResetPasswordActivity.class));
//                finish();

            }
        });

        txt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this , SignupActivity.class));
            }
        });


    }

    @Override
    public void showProgressBar() {
        progressButton.showProgressBar();
        btn_login.setEnabled(false);

        input_password.setEnabled(false);
        input_phone_number.setEnabled(false);
    }

    @Override
    public void hideProgressBar() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                btn_login.setEnabled(true);
                progressButton.hideProgressBar();

                input_password.setEnabled(true);
                input_phone_number.setEnabled(true);
            }
        });

    }

    @Override
    public void showErrorMessage(String Message) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toasty.info(LoginActivity.this, "" + Message).show();

            }
        });

    }

    @Override
    public void onSuccessMessage(String message) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                LocaleHelper.setLocale(LoginActivity.this, Common.CURRENT_USER.getLanguage().toLowerCase());

                PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_LOGIN, true);
                PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_PHONE, input_phone_number.getEditText().getText().toString());
                PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_PASSWORD, input_password.getEditText().getText().toString());

                startActivity(new Intent(LoginActivity.this, SelectDeliveryLocationActivity.class));


                finish();

            }
        });
    }

    @Override
    public void onSuccessLoginToMap() {
        Dexter.withActivity(LoginActivity.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                        isFirstTimeAddLocation = true;
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        finish();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void onErrorRole(String role) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //Now we need an AlertDialog.Builder object
                //setting the view of the builder to our custom view that we already inflated


                //finally creating the alert dialog and displaying it


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_dialog_alarm, null);
                builder.setView(dialogView);

//                img_done = dialogView.findViewById(R.id.img_done);
                Button btn_ok = dialogView.findViewById(R.id.btn_ok);


                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (role.equalsIgnoreCase(RoleEnum.PILOT.rawValue())) {

                            goAppInGooglePlay(Constants.PACKAGE_NAME_PILOT);

                        } else if (role.equalsIgnoreCase(RoleEnum.STOREADMIN.rawValue())) {
                            goAppInGooglePlay(Constants.PACKAGE_NAME_STOREADMIN);
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });


    }

    @Override
    public void OnSuspendedCallback() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this, SuspendActivity.class));
                overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
                isFirstTimeAddLocation = true;
                finish();
            }
        });

    }


    private void goAppInGooglePlay(String appPackageName) {

        Intent intent = getPackageManager().getLaunchIntentForPackage(appPackageName);

        if (intent == null) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else {
            startActivity(intent);

        }

    }
}
