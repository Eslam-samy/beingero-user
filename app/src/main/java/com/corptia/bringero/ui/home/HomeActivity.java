package com.corptia.bringero.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.home.ui.notification.NotificationFragment;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.corptia.bringero.ui.setting.main.SettingActivity;
import com.corptia.bringero.ui.cart.CartFragment;
import com.corptia.bringero.ui.home.ui.storetypes.StoreTypesFragment;
import com.corptia.bringero.ui.order.OrderFragment;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, SelectDeliveryLocationView {


    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.nav_bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    Fragment selectedFragment = null;


    Menu menu;

    @BindView(R.id.txt_location)
    TextView txt_location;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    //For Select Location
    BottomSheetDialog bottomSheetDialog;
    SelectDeliveryLocationAdapter adapter;
    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    CustomLoading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initToolbar(toolbar);
        initNavigationView();


        loading = new CustomLoading(this, true);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_SPEED_CART)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        } else
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //Set CurrentLocation
        setCurrentLocation();

        txt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = Common.showDialogSelectLocation(HomeActivity.this, bottomSheetDialog, presenter);
            }
        });

    }

    private void setCurrentLocation() {

        if (Common.CURRENT_USER != null) {
            if (Common.CURRENT_USER.getCurrentDeliveryAddress() != null) {

                String region = Common.CURRENT_USER.getCurrentDeliveryAddress().getRegion();
                String name = Common.CURRENT_USER.getCurrentDeliveryAddress().getName();

                if (name!=null && region!=null)
                txt_location.setText(new StringBuilder().append(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase())
                        .append(" (")
                        .append(region.substring(0, 1).toUpperCase() + region.substring(1).toLowerCase())
                        .append(")"));

            } else
                txt_location.setText(getString(R.string.select_location));
        }

    }

    void initNavigationView() {

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = menuItem -> {

        switch (menuItem.getItemId()) {

            case R.id.nav_home:
                if (!(selectedFragment instanceof StoreTypesFragment)) {
                    selectedFragment = new StoreTypesFragment();
                    getSupportActionBar().setTitle(R.string.gallery);
                    txt_location.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.nav_order:
                if (!(selectedFragment instanceof OrderFragment)) {
                    selectedFragment = new OrderFragment();
                    getSupportActionBar().setTitle(R.string.orders);
                    txt_location.setVisibility(View.GONE);
                    appbar.setBackgroundColor(getResources().getColor(R.color.white));
                    appbar.getContext().setTheme(R.style.AppBarLayoutTheme);
                }
                break;

            case R.id.nav_cart:
                if (!(selectedFragment instanceof CartFragment)) {
                    selectedFragment = new CartFragment();
                    getSupportActionBar().setTitle(R.string.cart);
                    txt_location.setVisibility(View.GONE);
                    appbar.setBackgroundColor(getResources().getColor(R.color.white));
                    appbar.getContext().setTheme(R.style.AppBarLayoutTheme);
                }
                break;

            case R.id.nav_notification:
                if (!(selectedFragment instanceof NotificationFragment)) {
                    selectedFragment = new NotificationFragment();
                    getSupportActionBar().setTitle(R.string.notification);
                    txt_location.setVisibility(View.GONE);
                    appbar.setBackgroundColor(getResources().getColor(R.color.white));
                    appbar.getContext().setTheme(R.style.AppBarLayoutTheme);
                }
                break;

            default:
                //ooooo

        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, selectedFragment)
                    .commit();
        }

        return true;
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingActivity.class));
                finish();
                break;

        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initToolbar(Toolbar toolbar) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onSuccessUpdateCurrentLocation() {
        runOnUiThread(() -> {
            bottomSheetDialog.dismiss();
            setCurrentLocation();
        });

    }

    @Override
    public void onSuccessUpdateNestedLocation() {

    }

    @Override
    public void onSuccessRemovedLocation() {

    }

    @Override
    public void showProgressBar() {

        loading.showProgressBar(this, false);
    }

    @Override
    public void hideProgressBar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.hideProgressBar();
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }
}
