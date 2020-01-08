package com.corptia.bringero.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.NotificationCountUnreadQuery;
import com.corptia.bringero.graphql.UpdateNotificationMutation;
import com.corptia.bringero.model.NotificationCount;
import com.corptia.bringero.type.NotificationFilterInput;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.home.ui.notification.NotificationFragment;
import com.corptia.bringero.ui.location.AllLocation.LocationsDeliveryActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationAdapter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.corptia.bringero.ui.setting.main.SettingActivity;
import com.corptia.bringero.ui.cart.CartFragment;
import com.corptia.bringero.ui.home.ui.storetypes.StoreTypesFragment;
import com.corptia.bringero.ui.order.OrderFragment;
import com.corptia.bringero.utils.CustomLoading;
import com.corptia.bringero.utils.PicassoUtils;
import com.corptia.bringero.utils.language.LocaleHelper;
import com.corptia.bringero.utils.recyclerview.decoration.LinearSpacingItemDecoration;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class HomeActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, SelectDeliveryLocationView {


    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.nav_bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    Fragment selectedFragment = null;

    @BindView(R.id.txt_location)
    TextView txt_location;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    //For Select Location
    BottomSheetDialog bottomSheetDialog;
    SelectDeliveryLocationAdapter adapter;
    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    CustomLoading loading;

    //For Count Notification
    View  notificationBadge;//notificationBadge
    TextView  txt_notificationsBadge; // txt_notificationsBadge
    BottomNavigationMenuView menuView;
    BottomNavigationItemView itemViewNotification;

    TextView txt_user_name,txt_user_phone ;
    CircleImageView img_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Common.CURRENT_USER!=null)
            LocaleHelper.setLocale(this, Common.CURRENT_USER.getLanguage().toLowerCase());
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        initToolbar(toolbar);
        initNavigationView();



        loading = new CustomLoading(this, true);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        itemViewNotification = (BottomNavigationItemView) menuView.getChildAt(3);


        if (getIntent() != null && getIntent().hasExtra(Constants.EXTRA_SPEED_CART)) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        } else
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

        //Set CurrentLocation
        setCurrentLocation();

        iniBadgeNotification();

        txt_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = Common.showDialogSelectLocation(HomeActivity.this, bottomSheetDialog, presenter);
            }
        });

        getDataUser();

    }

    private void getDataUser() {

        View headerView = navigationView.getHeaderView(0);
        txt_user_name = headerView.findViewById(R.id.txt_user_name);
        txt_user_phone = headerView.findViewById(R.id.txt_user_phone);
        img_avatar = headerView.findViewById(R.id.img_avatar);

        txt_user_name.setText(Common.CURRENT_USER.getFullName());
        txt_user_phone.setText(Common.CURRENT_USER.getPhone());

        if (Common.CURRENT_USER.getAvatarImageId()!=null)
            PicassoUtils.setImage(Common.BASE_URL_IMAGE + Common.CURRENT_USER.getAvatarName() , img_avatar);

    }


    private void iniBadgeNotification() {

        notificationBadge = LayoutInflater.from(this).inflate(R.layout.layout_notification_badge, menuView, false);
        txt_notificationsBadge = notificationBadge.findViewById(R.id.txt_notificationsBadge);
        itemViewNotification.addView(notificationBadge);
        notificationBadge.setVisibility(GONE);

        //countNotificationUnread();

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

                }
                break;

            case R.id.nav_cart:
                if (!(selectedFragment instanceof CartFragment)) {
                    selectedFragment = new CartFragment();
                    getSupportActionBar().setTitle(R.string.cart);
                    txt_location.setVisibility(View.GONE);
//                    appbar.setBackgroundColor(getResources().getColor(R.color.white));
//                    appbar.getContext().setTheme(R.style.AppBarLayoutTheme);
                }
                break;

            case R.id.nav_notification:
                if (!(selectedFragment instanceof NotificationFragment)) {
                    selectedFragment = new NotificationFragment();

                    getSupportActionBar().setTitle(R.string.notification);

                    txt_location.setVisibility(View.GONE);
                    notificationBadge.setVisibility(GONE);

                    updateNotification();
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

    private void updateNotification() {

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateNotificationMutation.builder().build())
                .enqueue(new ApolloCall.Callback<UpdateNotificationMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateNotificationMutation.Data> response) {

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.nav_addresses:
                startActivity(new Intent(this, LocationsDeliveryActivity.class));
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
//            super.onBackPressed();
            showExitDialog();
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
        countNotificationUnread();
    }


    private void countNotificationUnread() {

        NotificationFilterInput filter = NotificationFilterInput.builder().status("Unread").build();
        MyApolloClient.getApollowClientAuthorization().query(NotificationCountUnreadQuery.builder().filter(filter).build())
                .enqueue(new ApolloCall.Callback<NotificationCountUnreadQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<NotificationCountUnreadQuery.Data> response) {

                        NotificationCountUnreadQuery.@Nullable GetAll data = response.data().NotificationCountUnreadQuery().getAll();
                        if (data.status() == 200) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Update Number

                                    int count = data.pagination().totalDocs();
                                    if (count == 0) {
                                        notificationBadge.setVisibility(GONE);
                                    } else {notificationBadge.setVisibility(VISIBLE);
                                        txt_notificationsBadge.setText(""+ (count > 99 ? "99+" : count));
                                    }

                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void countNotification(NotificationCount notificationCount) {

        if (notificationCount != null) {
            countNotificationUnread();
        }

    }
}
