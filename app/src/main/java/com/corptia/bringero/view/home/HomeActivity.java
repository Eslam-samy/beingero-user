package com.corptia.bringero.view.home;

import android.content.Intent;
import android.os.Bundle;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.corptia.bringero.type.RoleEnum;
import com.corptia.bringero.view.Setting.main.SettingActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    // private AppBarConfiguration mAppBarConfiguration;
    // public static NavController navController;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;
    public NavController navController;


    @BindViews({R.id.nav_home, R.id.nav_gallery, R.id.nav_wishlist, R.id.nav_location, R.id.nav_order, R.id.nav_cart, R.id.nav_notifications, R.id.nav_discounts, R.id.nav_terms_conditions, R.id.nav_contact_us, R.id.nav_settings})
    MenuItem nav_home, nav_gallery, nav_wishlist, nav_location, nav_order, nav_cart, nav_notifications, nav_discounts, nav_terms_conditions, nav_contact_us, nav_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initToolbar();
        initNavigationView();


        // navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        ///navigationView.setNavigationItemSelectedListener(this);

        /*navigationView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navController.navigate(R.id.action_nav_home_to_gallaryFragment);*/

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

       /* DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
*/

        //---------------- Menu ----------------------
        Menu menu = navigationView.getMenu();
        MenuItem nav_home = menu.findItem(R.id.nav_home);

    }

    private void initNavigationView() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);  // Hostfragment
        NavInflater inflater = navHostFragment.getNavController().getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.mobile_navigation);
        //graph.setDefaultArguments(getIntent().getExtras());
        //graph.setStartDestination(R.id.gallaryFragment);
        if (Common.CURRENT_USER.roleName().rawValue().equalsIgnoreCase(RoleEnum.STOREADMIN.rawValue())) {

            graph.setStartDestination(R.id.nav_gallery);
        } else {
            graph.setStartDestination(R.id.nav_home);

        }
        navHostFragment.getNavController().setGraph(graph);
        //navHostFragment.getNavController().getGraph().setDefaultArguments(getIntent().getExtras());
        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navigationView, navHostFragment.getNavController());
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.getNavController(), drawerLayout);

    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
       /* NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, drawerLayout);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        menuItem.setChecked(true);

        drawerLayout.closeDrawers();

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.nav_settings:
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                break;

            case R.id.nav_cart:

                break;

        }
        return true;

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
