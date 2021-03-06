package com.corptia.bringero.ui.Main.login;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.ui.Main.resetPassword.ResetPasswordActivity;
import com.corptia.bringero.ui.Main.signup.SignupActivity;
import com.corptia.bringero.ui.Main.suspend.SuspendActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.allowLocation.AllowLocationActivity;
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

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static com.corptia.bringero.Common.Common.isFirstTimeAddLocation;

public class LoginActivity extends BaseActivity implements LoginContract.LoginView {

    @BindView(R.id.btn_login)
    View btn_login;
    @BindView(R.id.input_phone_number)
    EditText input_phone_number;
    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.txt_forgot_password)
    TextView txt_forgot_password;
    @BindView(R.id.txt_signUp)
    TextView txt_signUp;

    @BindView(R.id.btn_changelanguage)
    View btn_changelanguage;

    LoginPresenter loginPresenter;

    AlertDialog waitingDialog;

    //This Custom Button Loading
    ProgressButton progressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String oldLang = LocaleHelper.getLanguage(LoginActivity.this);
        Common.LOG("My Lang " + oldLang);

        ButterKnife.bind(this);
        progressButton = new ProgressButton(this, btn_login);
        waitingDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        loginPresenter = new LoginPresenter(this);

        btn_login.setOnClickListener(view1 -> {
            String phone = input_phone_number.getText().toString();
            String password = input_password.getText().toString();

/*
            switch (phone) {
                case "01003544497":
                case "01000100041":
                case "01000100042":
                case "01000100043":
                case "01029936932":
                    showTestingDialogue(phone, password);
                    break;
                default:
                    loginPresenter.onLogin(phone, password);
            }
*/
            loginPresenter.onLogin(phone, password);
            //HomeActivity .navController .navigate(R.id.action_loginFragment_to_nav_home2);
        });

        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
//                finish();

            }
        });

        txt_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btn_changelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView lang = btn_changelanguage.findViewById(R.id.lang);
                if (lang.getText().toString().equalsIgnoreCase("English")) {
                    setLocale("en");
                } else {
                    setLocale("ar");
                }

//                String oldLang = LocaleHelper.getLanguage(LoginActivity.this);
//
//                if (oldLang.equalsIgnoreCase("ar"))
//                    setLocale("en");
//                else
//                    setLocale("ar");

            }
        });


    }

    private void changeLanguage(String language) {

        Common.LOG("I Set : " + language);
        LocaleHelper.setLocale(this, language);
        recreate();
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

                Common.LOG("My Token " + Common.CURRENT_USER.getToken());

                saveLocalDataToPref();

                startActivity(new Intent(LoginActivity.this, SelectDeliveryLocationActivity.class));


                finish();

            }
        });
    }

    private void saveLocalDataToPref() {

        LocaleHelper.setLocale(LoginActivity.this, Common.CURRENT_USER.getLanguage().toLowerCase());

        PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_LOGIN, true);
        PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_PHONE, input_phone_number.getText().toString());
        PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_PASSWORD, input_password.getText().toString());
        PrefUtils.saveToPrefs(LoginActivity.this, PrefKeys.USER_TOKEN_API, Common.CURRENT_USER.getToken());
    }

    @Override
    public void onSuccessLoginToMap() {

        saveLocalDataToPref();
        startActivity(new Intent(LoginActivity.this, AllowLocationActivity.class));
        isFirstTimeAddLocation = true;
        finish();

    }

    @Override
    public void onErrorRole(String role) {
        runOnUiThread(() -> {
            //Now we need an AlertDialog.Builder object
            //setting the view of the builder to our custom view that we already inflated
            //finally creating the alert dialog and displaying i
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(LoginActivity.this);
            //then we will inflate the custom alert dialog xml that we created
            View dialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.layout_dialog_alarm, null);
            builder.setView(dialogView);

//                img_done = dialogView.findViewById(R.id.img_done);
            Button btn_ok = dialogView.findViewById(R.id.btn_ok);
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            btn_ok.setOnClickListener(view -> {
                if (role.equalsIgnoreCase(RoleEnum.PILOT.rawValue())) {
                    goAppInGooglePlay(Constants.PACKAGE_NAME_PILOT);
                } else if (role.equalsIgnoreCase(RoleEnum.STOREADMIN.rawValue())) {
                    goAppInGooglePlay(Constants.PACKAGE_NAME_STOREADMIN);
                }
                dialog.dismiss();
            });

            dialog.show();

        });
    }

    @Override
    public void OnSuspendedCallback() {
        runOnUiThread(() -> {
            startActivity(new Intent(LoginActivity.this, SuspendActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            isFirstTimeAddLocation = true;
            finish();
        });
    }

    private void showTestingDialogue(String phone, String password) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_testing));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String holderUrl = Common.BASE_URL;
                Common.BASE_URL = Common.TEST_URL;
                Common.TEST_URL = holderUrl;
                holderUrl = Common.BASE_URL_IMAGE;
                Common.BASE_URL_IMAGE = Common.TEST_URL_IMAGE;
                Common.TEST_URL_IMAGE = holderUrl;
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
                loginPresenter.onLogin(phone, password);
            }
        });
        builder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {
            if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
            loginPresenter.onLogin(phone, password);
        });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
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


    public void setLocale(String lang) {
//        Locale myLocale = new Locale(lang);
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//        conf.locale = myLocale;
//        res.updateConfiguration(conf, dm);
//        onConfigurationChanged(conf);
/*Intent refresh = new Intent(this, AndroidLocalize.class);
startActivity(refresh);*/

//        Common.LOG("Hi " + lang);
//
//
//        Resources res = getResources();
//// Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
//// Use conf.locale = new Locale(...) if targeting lower versions
//        res.updateConfiguration(conf, dm);

        Common.LOG("My Lang " + lang);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Common.LOG("My Lang " + lang);

        LocaleHelper.setLocale(this, lang);

        recreate();

//        Intent refresh = new Intent(this, LoginActivity.class);
//
//        finish();
//        startActivity(refresh);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
// Checks the active language
        if (newConfig.locale == Locale.ENGLISH) {
//            Toast.makeText(this, "English", Toast.LENGTH_SHORT).show();
        } else if (newConfig.locale == Locale.FRENCH) {
//            Toast.makeText(this, "French", Toast.LENGTH_SHORT).show();
        }
    }
}
