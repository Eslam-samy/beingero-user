package com.corptia.bringero.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.sharedPref.PrefKeys;
import com.corptia.bringero.utils.sharedPref.PrefUtils;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.Main.MainActivity;
import com.corptia.bringero.ui.Main.login.LoginContract;
import com.corptia.bringero.ui.Main.login.LoginPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import static com.corptia.bringero.Common.Common.isFirstTimeAddLocation;

public class SplashActivity extends BaseActivity implements LoginContract.LoginView {

    public static final int STARTUP_DELAY = 300;
    public static final int ANIM_ITEM_DURATION = 1000;
    public static final int ITEM_DELAY = 300;

    private boolean animationStarted = false;

    ShimmerFrameLayout container;

    LoginPresenter loginPresenter = new LoginPresenter(this);

    ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Common.TOKEN_FIREBASE = (String) PrefUtils.getFromPrefs(this, "user_token", "");


        setTheme(R.style.FullWindow);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        container = findViewById(R.id.shimmer_view_container1);
        container.startShimmer();


        img_logo = findViewById(R.id.img_logo);


//        //Set Lang
//        LocaleHelper.setLocale(SplashActivity.this, "ar");


        new Handler().postDelayed(() -> {

            boolean isLogin = (Boolean) PrefUtils.getFromPrefs(this, PrefKeys.USER_LOGIN, false);

            if (isLogin) {

                String phone = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_PHONE, "");
                String password = (String) PrefUtils.getFromPrefs(this, PrefKeys.USER_PASSWORD, "");

                loginPresenter.onLogin(phone, password);

            } else {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                //startActivity(intent,options.toBundle());
                startActivity(intent);
                finish();
            }


        }, 1000);
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

            startActivity(new Intent(SplashActivity.this, SelectDeliveryLocationActivity.class));

            finish();


        });

    }

    @Override
    public void onSuccessLoginToMap() {

        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        startActivity(new Intent(SplashActivity.this, MapsActivity.class));
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

    private void animate() {

        //ImageView logoImageView = (ImageView) findViewById(R.id.imlo);
        ViewGroup container = (ViewGroup) findViewById(R.id.root);

        ViewCompat.animate(container)
                .translationY(-250)
                .setStartDelay(STARTUP_DELAY)
                .setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new DecelerateInterpolator(1.2f)).start();

        for (int i = 0; i < container.getChildCount(); i++) {
            View v = container.getChildAt(i);
            ViewPropertyAnimatorCompat viewAnimator;

            if (!(v instanceof Button)) {
                viewAnimator = ViewCompat.animate(v)
                        .translationY(50).alpha(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(1000);
            } else {
                viewAnimator = ViewCompat.animate(v)
                        .scaleY(1).scaleX(1)
                        .setStartDelay((ITEM_DELAY * i) + 500)
                        .setDuration(500);
            }

            viewAnimator.setInterpolator(new DecelerateInterpolator()).start();
        }
    }

}
