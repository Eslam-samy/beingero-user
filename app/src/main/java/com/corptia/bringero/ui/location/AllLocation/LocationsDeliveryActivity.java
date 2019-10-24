package com.corptia.bringero.ui.location.AllLocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.Interface.IOnRecyclerViewClickListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Utils.recyclerview.SwipeToDeleteCallback;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.location.addNewLocation.AddNewLocationActivity;
import com.corptia.bringero.ui.locations.LocationAdapter;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class LocationsDeliveryActivity extends BaseActivity implements LocationsDeliveryView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_location)
    RecyclerView recycler_location;

    LocationAdapter adapter;

    @BindView(R.id.root)
    LinearLayout root;

    Snackbar snackbar;
    int position = 0;
    MeQuery.DeliveryAddress item = null;

    LocationsDeliveryPresenter presenter = new LocationsDeliveryPresenter(this);

    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_delivery);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.location));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchData();
    }

    void FetchData() {

        adapter = new LocationAdapter(this, Common.CURRENT_USER.deliveryAddresses());
        recycler_location.setLayoutManager(new LinearLayoutManager(this));
        recycler_location.setHasFixedSize(true);

        recycler_location.setAdapter(adapter);

        adapter.setClickListener(new IOnRecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {


                MeQuery.DeliveryAddress address = adapter.getItems(position);
                Intent intent = new Intent(LocationsDeliveryActivity.this, AddNewLocationActivity.class);

                intent.putExtra(Constants.EXTRA_ADDRESS_NAME, address.name());
                intent.putExtra(Constants.EXTRA_ADDRESS_ID, address._id());
                intent.putExtra(Constants.EXTRA_ADDRESS_FLAT_TYPE, address.flatType().rawValue());
                intent.putExtra(Constants.EXTRA_ADDRESS_REGION, address.region());
                intent.putExtra(Constants.EXTRA_ADDRESS_FLAT, address.flat());
                intent.putExtra(Constants.EXTRA_ADDRESS_FLOOR, address.floor());
                intent.putExtra(Constants.EXTRA_ADDRESS_BUILDING, address.building());
                intent.putExtra(Constants.EXTRA_ADDRESS_STREET, address.street());
                intent.putExtra(Constants.EXTRA_LATITUDE, address.locationPoint().lat());
                intent.putExtra(Constants.EXTRA_LONGITUDE, address.locationPoint().lng());
                intent.putExtra(Constants.EXTRA_UPDATE, "UPDATE");

                //intent.putExtra(Constants.EXTRA_ADDRESS_CITY_ID,address.cityId());

                startActivity(intent);
            }
        });

        enableSwipeToDeleteAndUndo();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                position = viewHolder.getAdapterPosition();
                item = adapter.getItems(position);
                presenter.removeItems(item._id());

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recycler_location);
    }

    @Override
    public void onSuccessUpdateCurrentLocation() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.restoreItem(item, position);
                recycler_location.scrollToPosition(position);
            }
        });

    }

    @Override
    public void onSuccessUpdateNestedLocation() {

    }

    @Override
    public void showProgressBar() {

        dialog.show();
    }

    @Override
    public void hideProgressBar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void showErrorMessage(String Message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.error(LocationsDeliveryActivity.this, Message);
            }
        });
    }

    @Override
    public void onSuccessMessage(String message) {

    }

    @Override
    public void onSuccessRemovedLocation() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter.removeItem(position);


                snackbar = Snackbar
                        .make(root, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        presenter.addNewAddress(item.name(),
                                item.region(),
                                item.street(),
                                item.flatType(),
                                item.floor(), item.flat(),
                                item.building(),
                                item.locationPoint().lat(),
                                item.locationPoint().lng(),
                                false);


                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu_add:
                Intent intent = new Intent(LocationsDeliveryActivity.this , MapsActivity.class);
                intent.putExtra(Constants.EXTRA_IS_UPDATE_CURRENT_LOCATION  , false);
                Common.isUpdateCurrentLocation = false;
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    //When Back Arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
