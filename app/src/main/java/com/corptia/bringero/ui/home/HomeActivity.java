package com.corptia.bringero.ui.home;

import android.content.Intent;
import android.os.Bundle;

import com.corptia.bringero.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.corptia.bringero.Utils.lib.CustomBottomNavigationView;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.ui.Setting.main.SettingActivity;
import com.corptia.bringero.ui.cart.CartFragment;
import com.corptia.bringero.ui.home.ui.gallery.GalleryFragment;
import com.corptia.bringero.ui.home.ui.storetypes.StoreTypesFragment;
import com.corptia.bringero.ui.order.OrderFragment;
import com.corptia.bringero.ui.storesDetail.StoreDetailFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initToolbar(toolbar);
        initNavigationView();

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);


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

}
