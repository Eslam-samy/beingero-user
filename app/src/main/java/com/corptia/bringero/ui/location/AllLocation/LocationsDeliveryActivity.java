package com.corptia.bringero.ui.location.AllLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.model.DeliveryAddresses;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.utils.recyclerview.SwipeToDeleteCallback;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.location.addNewLocation.AddNewLocationActivity;
import com.corptia.bringero.ui.locations.LocationAdapter;
import com.google.android.material.snackbar.Snackbar;
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

public class LocationsDeliveryActivity extends BaseActivity implements LocationsDeliveryView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_location)
    RecyclerView recycler_location;
    @BindView(R.id.btn_add)
    TextView btn_add;

    LocationAdapter adapter;

    @BindView(R.id.root)
    LinearLayout root;

    Snackbar snackbar;
    int position = 0;

    boolean isClickUndo;

    LocationsDeliveryPresenter presenter = new LocationsDeliveryPresenter(this);

    AlertDialog dialog;
    DeliveryAddresses item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_delivery);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.location));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAddress();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchData();
    }

    void FetchData() {

        adapter = new LocationAdapter(this, Common.CURRENT_USER.getDeliveryAddressesList());
        recycler_location.setLayoutManager(new LinearLayoutManager(this));
        recycler_location.setHasFixedSize(true);

        recycler_location.setAdapter(adapter);

        adapter.setClickListener((view, position) -> {

            presenter.userUpdateCurrentLocation(adapter.getItems(position).getId());

            adapter.selectCurrentLocation(position);

        });

        adapter.setClickListenerUpdate((view, position) -> {

            DeliveryAddresses address = adapter.getItems(position);
            Intent intent = new Intent(LocationsDeliveryActivity.this, AddNewLocationActivity.class);

            intent.putExtra(Constants.EXTRA_ADDRESS_NAME, address.getName());
            intent.putExtra(Constants.EXTRA_ADDRESS_ID, address.getId());
            intent.putExtra(Constants.EXTRA_ADDRESS_FLAT_TYPE, address.getFlatType());
            intent.putExtra(Constants.EXTRA_ADDRESS_REGION, address.getRegion());
            intent.putExtra(Constants.EXTRA_ADDRESS_FLAT, address.getFlat());
            intent.putExtra(Constants.EXTRA_ADDRESS_FLOOR, address.getFloor());
            intent.putExtra(Constants.EXTRA_ADDRESS_BUILDING, address.getBuilding());
            intent.putExtra(Constants.EXTRA_ADDRESS_STREET, address.getStreet());
            intent.putExtra(Constants.EXTRA_LATITUDE, address.getLocation().latitude);
            intent.putExtra(Constants.EXTRA_LONGITUDE, address.getLocation().longitude);
            intent.putExtra(Constants.EXTRA_ADDRESS_POSITION, position);
            intent.putExtra(Constants.EXTRA_UPDATE, "UPDATE");

            //intent.putExtra(Constants.EXTRA_ADDRESS_CITY_ID,address.cityId());

            startActivity(intent);

        });

//        enableSwipeToDeleteAndUndo();
    }

//    private void enableSwipeToDeleteAndUndo() {
//        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//
//
//                position = viewHolder.getAdapterPosition();
//                item = adapter.getItems(position);
//                Common.CURRENT_USER.getDeliveryAddressesList().remove(position);
//                presenter.removeItems(item.getId());
//
//            }
//        };
//
//        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
//        itemTouchhelper.attachToRecyclerView(recycler_location);
//    }

    @Override
    public void onSuccessUpdateCurrentLocation() {


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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.error(LocationsDeliveryActivity.this, Message).show();
            }
        });
    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void onSuccessRemovedLocation() {


//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                adapter.removeItem(position);
//
//
//                snackbar = Snackbar
//                        .make(root, "Item was removed from the list.", Snackbar.LENGTH_LONG);
//                snackbar.setAction("UNDO", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                         isClickUndo = true;
//
//                        presenter.addNewAddress(item.getName(),
//                                item.getRegion(),
//                                item.getStreet(),
//                                item.getFlatType().equalsIgnoreCase(FlatType.HOUSE.rawValue()) ? FlatType.HOUSE : FlatType.OFFICE,
//                                item.getFloor(), item.getFlat(),
//                                item.getBuilding(),
//                                item.getLocation().latitude,
//                                item.getLocation().longitude,
//                                false);
//
//
//                    }
//                });
//
//                snackbar.setActionTextColor(Color.YELLOW);
//                snackbar.show();
//
//            }
//        });


    }

    @Override
    public void onUndoLocation(String newId) {

//        if (isClickUndo) {
//            runOnUiThread(() -> {
//                item.setId(newId);
//                adapter.restoreItem(item, position);
//                recycler_location.scrollToPosition(position);
//            });
//            isClickUndo = false;
//        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_location, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.action_menu_add:
//
//
//
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//
//    }

    //When Back Arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    void addNewAddress(){

        Dexter.withActivity(LocationsDeliveryActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        Intent intent = new Intent(LocationsDeliveryActivity.this , MapsActivity.class);
                        intent.putExtra(Constants.EXTRA_IS_UPDATE_CURRENT_LOCATION  , false);
                        Common.isUpdateCurrentLocation = false;
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        Toasty.error(LocationsDeliveryActivity.this , "onPermissionDenied").show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();
    }
}
