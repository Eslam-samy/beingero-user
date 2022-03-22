package com.corptia.bringero.ui.home;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.corptia.bringero.Common.Common.isFirstTimeGetCartCount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivityHomeModefiedBinding;
import com.corptia.bringero.graphql.NotificationCountUnreadQuery;
import com.corptia.bringero.type.NotificationFilterInput;
import com.corptia.bringero.ui.home.order.MyOrdersActivity;
import com.corptia.bringero.ui.home.setting.main.SettingActivity;
import com.corptia.bringero.ui.home.storetypes.StoreTypesFragment;
import com.corptia.bringero.ui.location.AllLocation.LocationsDeliveryActivity;
import com.corptia.bringero.ui.webview.WebViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HomeModefiedActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    static ActivityHomeModefiedBinding binding;
    ActionBarDrawerToggle toggle;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    NavController navController;
    ImageView img_avatar;
    TextView txt_user_name, txt_user_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_modefied);
        initRemoteConfig();
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        initNav();

        binding.fab.setOnClickListener(view -> {
            navController.navigate(R.id.cartFragment);
        });

        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_SPEED_CART)) {
            navController.navigate(R.id.cartFragment);
        }
        binding.navView.setNavigationItemSelectedListener(this);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.cartFragment:
                case R.id.notificationFragment:
                case R.id.orderFragment:
                case R.id.settingsFragment:
                    binding.fab.setVisibility(View.GONE);
                    break;
                default:
                    binding.fab.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint("WrongConstant")
    public static void showDrawer() {
        binding.drawer.openDrawer(Gravity.START);
    }

    private void initNav() {
        toggle = new ActionBarDrawerToggle(this, binding.drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            //------------------- drawer animation ------------------------------------------------------>
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (!Common.CURRENT_USER.getLanguage().isEmpty()) {
                    if (Common.CURRENT_USER.getLanguage().equalsIgnoreCase("en")) {
                        binding.line1.setX(binding.navView.getWidth() * slideOffset * .75f);
                        binding.line1.setScaleY(1 - (slideOffset * 0.27f));
                        binding.line1.setScaleX(1 - (slideOffset * 0.4f));
                        binding.drawer.setDrawerElevation(0);

                        binding.drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
                    } else
                        binding.line1.setX(binding.navView.getWidth() * slideOffset * -.82f);
                    binding.line1.setScaleY(1 - (slideOffset * 0.22f));
                    binding.line1.setScaleX(1 - (slideOffset * 0.3f));
                    binding.drawer.setDrawerElevation(0);
                    binding.drawer.setScrimColor(getResources().getColor(android.R.color.transparent));

                }

            }
        };

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));


        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!navController.popBackStack()) {
            showExitDialog();
        }
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void showExitDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_to_exit));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataUser();

        isFirstTimeGetCartCount = true;
        Common.GetCartItemsCount(null);

        checkNewVersion();

    }

    private void getDataUser() {

        View headerView = binding.navView.getHeaderView(0);
        txt_user_name = headerView.findViewById(R.id.txt_user_name);
        txt_user_phone = headerView.findViewById(R.id.txt_user_phone);
        img_avatar = headerView.findViewById(R.id.img_avatar);

        txt_user_name.setText(Common.CURRENT_USER.getFullName());
        txt_user_phone.setText(Common.CURRENT_USER.getPhone());
        if (Common.CURRENT_USER.getAvatarImageId() != null)
            Picasso.get().load(Common.BASE_URL_IMAGE + Common.CURRENT_USER.getAvatarName())
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(img_avatar);

//                    .resize(1, 1)
//                    .noFade()
//                    .memoryPolicy(MemoryPolicy.NO_CACHE )
//                    .networkPolicy(NetworkPolicy.NO_CACHE)
//            .error(R.drawable.ic_placeholder_profile)


        //Set CurrentLocation

    }


    private void checkNewVersion() {


        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {

                        if (task.isSuccessful()) {

//                                boolean updated = task.getResult();

//                            mFirebaseRemoteConfig.activateFetched();
                            mFirebaseRemoteConfig.activate();

                            Common.LAST_APP_VERSION = mFirebaseRemoteConfig.getDouble(Constants.APP_VERSION);

                            if (Common.LAST_APP_VERSION > getCurrentVersionCode()) {

                                //Must Update You App
                                showUpdateDialog();

                            } else {
                                //No need To Update
//                                Common.LOG("NO NEED TO UPDATE " + Common.LAST_APP_VERSION + " ---- " + getCurrentVersionCode());
                            }

                        } else {

                        }
                    }
                });
    }

    private void initRemoteConfig() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(30) //TODO This For Wait Before Update
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

    }

    private void showUpdateDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(HomeModefiedActivity.this);
        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(HomeModefiedActivity.this).inflate(R.layout.layout_dialog_update, null);
        builder.setView(dialogView);

//                img_done = dialogView.findViewById(R.id.img_done);
        Button btn_update = dialogView.findViewById(R.id.btn_update);
        androidx.appcompat.app.AlertDialog dialog = builder.create();

//        dialog.getWindow().setLayout(600, 400); //Controlling width and height.

        dialog.setCancelable(false);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start Activity To App
                goAppInGooglePlay();
            }
        });
        dialog.show();
    }

    private void goAppInGooglePlay() {

        String appPackageName = getPackageName();

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException ex) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    private double getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        switch (id) {
            case R.id.nav_settings:
                intent = new Intent(this, SettingActivity.class);
                break;
            case R.id.nav_orders:
                intent = new Intent(this, MyOrdersActivity.class);
                break;
            case R.id.nav_addresses:
                intent = new Intent(this, LocationsDeliveryActivity.class);
                break;
            case R.id.nav_contact:
                intent = new Intent(this, com.corptia.bringero.ui.home.setting.contactUs.ContactUsActivity.class);
                break;
            case R.id.nav_terms_conditions:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(Constants.EXTRA_TERMS_CONDITIONS, "EXTRA_TERMS_CONDITIONS");
                break;
//            case R.id.nav_privacy_policy:
//                intent = new Intent(this, WebViewActivity.class);
//                intent.putExtra(Constants.EXTRA_PRIVACY_POLICY, "EXTRA_PRIVACY_POLICY");
//                break;
//            case R.id.nav_faq_support:
//                intent = new Intent(this, WebViewActivity.class);
//                intent.putExtra(Constants.EXTRA_FAQ_SUPPORT, "EXTRA_FAQ_SUPPORT");
//                break;

            default:
                binding.drawer.closeDrawer(GravityCompat.START);
                return true;

        }

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        binding.drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}