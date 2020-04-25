package com.corptia.bringero.ui.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.Main.comingSoon.ComingSoonActivity;
import com.corptia.bringero.ui.Main.login.LoginActivity;
import com.corptia.bringero.ui.Main.suspend.SuspendActivity;
import com.corptia.bringero.ui.Main.underMaintenance.UnderMaintenanceActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.allowLocation.AllowLocationActivity;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.Main.MainActivity;
import com.corptia.bringero.ui.Main.login.LoginContract;
import com.corptia.bringero.ui.Main.login.LoginPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.corptia.bringero.Common.Common.isFirstTimeAddLocation;

public class SplashActivity extends BaseActivity implements LoginContract.LoginView {

    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;

    private boolean animationStarted = false;

    ShimmerFrameLayout container;

    LoginPresenter loginPresenter = new LoginPresenter(this);

    ImageView img_logo;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        setTheme(R.style.FullWindow);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Common.TOKEN_FIREBASE = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_TOKEN_FIREBASE, "");

        Common.CURRENT_IMIE = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        initRemoteConfig();

        container = findViewById(R.id.shimmer_view_container1);
        container.startShimmer();


        img_logo = findViewById(R.id.img_logo);


//        //Set Lang
//        LocaleHelper.setLocale(SplashActivity.this, "ar");


        new Handler().postDelayed(() -> {

            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {

                            if (task.isSuccessful()) {

//                                boolean updated = task.getResult();

//                            mFirebaseRemoteConfig.activateFetched();
                                mFirebaseRemoteConfig.activate();

                                Common.BASE_URL = mFirebaseRemoteConfig.getString(Constants.BASE_URL) + "graphql";
                                Common.BASE_URL_IMAGE = mFirebaseRemoteConfig.getString(Constants.BASE_URL) + "images/";
                                PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.FULL_BASE_URL, Common.BASE_URL);
                                PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.FULL_BASE_URL_IMAGE, Common.BASE_URL_IMAGE);

                                /*Common.TEST_URL = mFirebaseRemoteConfig.getString(Constants.TEST_URL) + "graphql";
                                Common.TEST_URL_IMAGE = mFirebaseRemoteConfig.getString(Constants.TEST_URL) + "images/";
                                PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.FULL_TEST_URL, Common.TEST_URL);
                                PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.FULL_TEST_URL_IMAGE, Common.TEST_URL_IMAGE);*/


//                                Common.BASE_URL_IMAGE_UPLOAD = mFirebaseRemoteConfig.getString(Constants.BASE_URL)+"images/";

                                boolean under_maintenance = mFirebaseRemoteConfig.getBoolean(Constants.UNDER_MAINTENANCE);
                                boolean isComingSoon = mFirebaseRemoteConfig.getBoolean(Constants.IS_COMING_SOON);

                                Common.LAST_APP_VERSION = mFirebaseRemoteConfig.getDouble(Constants.APP_VERSION);

                                if (isComingSoon) {

                                    String dateSoon = mFirebaseRemoteConfig.getString(Constants.COUNT_DOWNDATE);

                                    if (!dateSoon.isEmpty()) {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("timestamp");
                                        Map<String, Object> value = new HashMap<>();
                                        value.put("timestamp", ServerValue.TIMESTAMP);
                                        ref.setValue(value);


                                        Date date = null;
                                        try {
                                            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateSoon);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        long milliseconds = date.getTime();


                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                Map map = (Map) dataSnapshot.getValue();
                                                long currentTimeFromFirebase = (long) map.get("timestamp");


//                                                long millisecondsFromNow = milliseconds - (new Date()).getTime();
                                                long millisecondsFromNow = milliseconds - currentTimeFromFirebase;

                                                Intent intent;
                                                if (millisecondsFromNow <= 0) {
                                                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                                                } else {
                                                    intent = new Intent(SplashActivity.this, ComingSoonActivity.class);
                                                    intent.putExtra("millisecondsFromNow", millisecondsFromNow);
                                                    //Here Start Activity To comingSoon
                                                }

                                                startActivity(intent);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                finish();

//                                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                            String dateString = formatter.format(new Date(currentTimeFromFirebase));
//                                            Common.LOG("HAZEM" + dateString);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }


                                } else if (under_maintenance) {

                                    //Here Start Activity To under_maintenance
                                    startActivity(new Intent(SplashActivity.this, UnderMaintenanceActivity.class));
                                    finish();

                                } else {
                                    checkLoginUser();
                                }

                            } else {
                            }
                        }
                    });


        }, 1000);

    }

    private void checkLoginUser() {

        boolean isLogin = (Boolean) PrefUtils.getFromPrefs(this, PrefKeys.USER_LOGIN, false);

        if (isLogin) {

            String phone = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_PHONE, "");
            String password = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_PASSWORD, "");
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

        } else {

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            //startActivity(intent,options.toBundle());
            startActivity(intent);
            finish();
        }


    }

    private void showTestingDialogue(String phone, String password) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
                loginPresenter.onLogin(phone, password);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.stopShimmer();
    }

    @Override
    public void showProgressBar() {

    }

    @Override
    public void hideProgressBar() {

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

        runOnUiThread(() -> {

            LocaleHelper.setLocale(SplashActivity.this, Common.CURRENT_USER.getLanguage().toLowerCase());

            PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.USER_TOKEN_API, Common.CURRENT_USER.getToken());

            startActivity(new Intent(SplashActivity.this, SelectDeliveryLocationActivity.class));

            finish();


        });

    }

    @Override
    public void onSuccessLoginToMap() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                PrefUtils.saveToPrefs(SplashActivity.this, PrefKeys.USER_TOKEN_API, Common.CURRENT_USER.getToken());
                startActivity(new Intent(SplashActivity.this, AllowLocationActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                isFirstTimeAddLocation = true;
                finish();

            }
        });

    }

    @Override
    public void onErrorRole(String role) {

    }

    @Override
    public void OnSuspendedCallback() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashActivity.this, SuspendActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                isFirstTimeAddLocation = true;
                finish();
            }
        });

    }

    //For Open Splash Fast
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus || animationStarted) {
            return;
        }

        //animate();

        container.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));


        super.onWindowFocusChanged(hasFocus);
    }


    private void initRemoteConfig() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(30) //TODO This For Wait Before Update
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

    }

//    private void animate() {
//
//        //ImageView logoImageView = (ImageView) findViewById(R.id.imlo);
//        ViewGroup container = (ViewGroup) findViewById(R.id.root);
//
//        ViewCompat.animate(container)
//                .translationY(-250)
//                .setStartDelay(STARTUP_DELAY)
//                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
//                new DecelerateInterpolator(1.2f)).start();
//
//        for (int i = 0; i < container.getChildCount(); i++) {
//            View v = container.getChildAt(i);
//            ViewPropertyAnimatorCompat viewAnimator;
//
//            if (!(v instanceof Button)) {
//                viewAnimator = ViewCompat.animate(v)
//                        .translationY(50).alpha(1)
//                        .setStartDelay((ITEM_DELAY * i) + 500)
//                        .setDuration(1000);
//            } else {
//                viewAnimator = ViewCompat.animate(v)
//                        .scaleY(1).scaleX(1)
//                        .setStartDelay((ITEM_DELAY * i) + 500)
//                        .setDuration(500);
//            }
//
//            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
//        }
//    }

}
