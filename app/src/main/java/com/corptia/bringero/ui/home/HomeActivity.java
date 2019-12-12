package com.corptia.bringero.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.corptia.bringero.ui.setting.main.SettingActivity;
import com.corptia.bringero.ui.cart.CartFragment;
import com.corptia.bringero.ui.home.ui.storetypes.StoreTypesFragment;
import com.corptia.bringero.ui.order.OrderFragment;
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
    //@BindViews({R.id.nav_home, R.id.nav_gallery, R.id.nav_wishlist, R.id.nav_location, R.id.nav_order, R.id.nav_cart, R.id.nav_notifications, R.id.nav_discounts, R.id.nav_terms_conditions, R.id.nav_contact_us, R.id.nav_settings})
    MenuItem nav_home, nav_gallery, nav_wishlist, nav_location, nav_order, nav_cart, nav_notifications, nav_discounts,
            nav_terms_conditions, nav_contact_us, nav_settings, nav_pricing;

    @BindView(R.id.txt_location)
    TextView txt_location;

    //For Select Location
    BottomSheetDialog bottomSheetDialog;
    SelectDeliveryLocationAdapter adapter;
    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initToolbar(toolbar);
        initNavigationView();

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //Set CurrentLocation
        setCurrentLocation();

        txt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogSelectLocation();

            }
        });

    }

    private void setCurrentLocation() {

        if (Common.CURRENT_USER!=null) {
            if (Common.CURRENT_USER.currentDeliveryAddress() != null) {

                String region = Common.CURRENT_USER.currentDeliveryAddress().region();
                String name = Common.CURRENT_USER.currentDeliveryAddress().name();

                txt_location.setText(new StringBuilder().append(name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase())
                        .append(" (")
                        .append(region.substring(0,1).toUpperCase() + region.substring(1).toLowerCase())
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
                }
                break;

            case R.id.nav_order:
                if (!(selectedFragment instanceof OrderFragment)) {
                    selectedFragment = new OrderFragment();
                    getSupportActionBar().setTitle(R.string.orders);
                }
                break;

            case R.id.nav_cart:
                if (!(selectedFragment instanceof CartFragment)) {
                    selectedFragment = new CartFragment();
                    getSupportActionBar().setTitle(R.string.cart);
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





    private void defineItems() {

        nav_home = menu.findItem(R.id.nav_home);
        nav_gallery = menu.findItem(R.id.nav_gallery);
        nav_cart = menu.findItem(R.id.nav_cart);
        nav_contact_us = menu.findItem(R.id.nav_contact_us);
        nav_discounts = menu.findItem(R.id.nav_discounts);
        nav_location = menu.findItem(R.id.nav_location);
        nav_notifications = menu.findItem(R.id.nav_notifications);
        nav_order = menu.findItem(R.id.nav_order);
        nav_settings = menu.findItem(R.id.nav_settings);
        nav_terms_conditions = menu.findItem(R.id.nav_terms_conditions);
        nav_wishlist = menu.findItem(R.id.nav_wishlist);
        nav_pricing = menu.findItem(R.id.nav_pricing);

    }

    private void initToolbar(Toolbar toolbar) {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }




    //    @Override
//    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//
//        if (destination.getId() == R.id.nav_settings) {
//            toolbar.setVisibility(View.GONE);
//            Log.d("HAZEM" , "YOU CLICK SETTING");
//
//        } else {
//            toolbar.setVisibility(View.VISIBLE);
//            Log.d("HAZEM" , "BACK");
//        }
//
//        initToolbar(toolbar);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void showDialogSelectLocation() {

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setTitle(getString(R.string.set_location));
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_select_delivery_location, null);

        RecyclerView recycler_delivery_location = sheetView.findViewById(R.id.recycler_delivery_location);
        Button btn_select_location_from_map = sheetView.findViewById(R.id.btn_select_location_from_map);

        recycler_delivery_location.setHasFixedSize(true);
        recycler_delivery_location.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectDeliveryLocationAdapter(this, Common.CURRENT_USER.deliveryAddresses());
        recycler_delivery_location.setAdapter(adapter);

        adapter.setClickListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Here Update Location Yo CuttentLocation

                presenter.userUpdateCurrentLocation(adapter.getCurrentDeliveryAddressID(position));
            }
        });

        btn_select_location_from_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here Open Maps
                Common.isUpdateCurrentLocation = true;
                startActivity(new Intent(HomeActivity.this , MapsActivity.class));
                bottomSheetDialog.dismiss();
            }
        });

//        bottomSheetDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_up_bottom_sheet);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
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

    }
}
