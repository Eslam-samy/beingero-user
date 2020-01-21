package com.corptia.bringero.ui.location.addNewLocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.model.FlatTypeModel;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.ui.MapWork.MapsActivity;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.ui.location.deliveryLocation.SelectDeliveryLocationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class AddNewLocationActivity extends BaseActivity implements SelectDeliveryLocationView, OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_region)
    TextInputLayout input_region;
    @BindView(R.id.input_address_name)
    TextInputLayout input_address_name;
    @BindView(R.id.spinner_flatType)
    Spinner spinner_flatType;
    @BindView(R.id.input_street)
    TextInputLayout input_street;
    @BindView(R.id.input_building)
    TextInputLayout input_building;
    @BindView(R.id.input_floor)
    TextInputLayout input_floor;
    @BindView(R.id.input__flat)
    TextInputLayout input_flat;
    @BindView(R.id.btn_save)
    Button btn_save;

    SelectDeliveryLocationPresenter presenter = new SelectDeliveryLocationPresenter(this);

    AlertDialog alertDialog;

    Intent intent;

    double latitude, longitude;

    List<FlatTypeModel> flatTypeList;
    FlatTypeModel flatTypeModel;

    //For Map
    private GoogleMap mMap;
    Marker marker;

    FlatType mflatType;

    //For Check if user no have any location and add new one must after add update current location
    boolean isUpdateCurrentLocation;
    String name, _id_Address, flatType, region, street;
    int flat, floor, building;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_location);

        initView();
        init();

        btn_save.setOnClickListener(view -> {

            //Here Some Valadation
            String region = input_region.getEditText().getText().toString();
            String address_name = input_address_name.getEditText().getText().toString();
            String street = input_street.getEditText().getText().toString();
            String building = input_building.getEditText().getText().toString();
            String floor = input_floor.getEditText().getText().toString();
            String flat = input_flat.getEditText().getText().toString();

            int buildingNumber,floorNumber , flatNumber;

            if (region.trim().equals("") || region.isEmpty()) {
                showMessage("region is required");
                return;
            } else if (address_name.trim().equals("") || address_name.isEmpty()) {
                showMessage("name is required");
                return;
            } else if (street.trim().equals("") || street.isEmpty()) {
                showMessage("street is required");
                return;
            }

            if (building.trim().equals("") || building.isEmpty()) {
//                showMessage("building is required");
//                return;
                buildingNumber = 0;
                floorNumber = 0;
                flatNumber=0;
            }
            else{

                buildingNumber = Integer.parseInt(building);

                if (floor.trim().equals("") || floor.isEmpty()) {

                showMessage("floor is required");
                return;
                } else{
                    floorNumber = Integer.parseInt(floor);

                }

                if (flat.trim().equals("") || flat.isEmpty()) {

                showMessage("flat is required");
                return;
                }
                else{

                    flatNumber = Integer.parseInt(floor);

                }

            }






            if (getIntent().hasExtra(Constants.EXTRA_UPDATE)) {
                presenter.updateLocation(_id_Address,
                        address_name,
                        region,
                        street,
                        mflatType,
                        floorNumber,
                        flatNumber,
                        buildingNumber,
                        latitude,
                        longitude);
            } else {
                presenter.addNewAddress(address_name,
                        region,
                        street,
                        mflatType,
                        floorNumber,
                        flatNumber,
                        buildingNumber,
                        latitude,
                        longitude,
                        Common.isUpdateCurrentLocation);
            }


        });

        spinner_flatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                flatTypeModel = (FlatTypeModel) parent.getSelectedItem();
                mflatType = flatTypeModel.getFlatType();
                // Log.d("onItemSelected", "" + language.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void init() {

        fillSpinnerLanguage();

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        intent = getIntent();
        if (intent != null) {

            if (intent.hasExtra(Constants.EXTRA_UPDATE)) {

                getSupportActionBar().setTitle(R.string.update_location);

                _id_Address = intent.getStringExtra(Constants.EXTRA_ADDRESS_ID);
                name = intent.getStringExtra(Constants.EXTRA_ADDRESS_NAME);
                flatType = intent.getStringExtra(Constants.EXTRA_ADDRESS_FLAT_TYPE);
                region = intent.getStringExtra(Constants.EXTRA_ADDRESS_REGION);
                street = intent.getStringExtra(Constants.EXTRA_ADDRESS_STREET);
                flat = intent.getIntExtra(Constants.EXTRA_ADDRESS_FLAT, 0);
                floor = intent.getIntExtra(Constants.EXTRA_ADDRESS_FLOOR, 0);
                building = intent.getIntExtra(Constants.EXTRA_ADDRESS_BUILDING, 0);

                //Set Defult Value
                input_address_name.getEditText().setText(name);
                input_region.getEditText().setText(region);
                input_street.getEditText().setText(street);
                input_flat.getEditText().setText("" + flat);
                input_floor.getEditText().setText("" + floor);
                input_building.getEditText().setText("" + building);

                latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);
                isUpdateCurrentLocation = intent.getBooleanExtra(Constants.EXTRA_IS_UPDATE_CURRENT_LOCATION, true);


            } else {

                getSupportActionBar().setTitle(R.string.add_new_location);


                latitude = intent.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                longitude = intent.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);
                isUpdateCurrentLocation = intent.getBooleanExtra(Constants.EXTRA_IS_UPDATE_CURRENT_LOCATION, false);
            }

        }


    }

    private void initView() {

        ButterKnife.bind(this);

        alertDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void fillSpinnerLanguage() {

        flatTypeList = new ArrayList<>();
        flatTypeList.add(new FlatTypeModel(FlatType.HOUSE));
        flatTypeList.add(new FlatTypeModel(FlatType.OFFICE));

        //fill data in spinner
        ArrayAdapter<FlatTypeModel> adapter = new ArrayAdapter<FlatTypeModel>(this, android.R.layout.simple_spinner_dropdown_item, flatTypeList);
        spinner_flatType.setAdapter(adapter);

    }

    @Override
    public void onSuccessUpdateCurrentLocation() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (Common.isUpdateCurrentLocation || Common.isFirstTimeAddLocation) {
                    Common.isFirstTimeAddLocation = false;
                    startActivity(new Intent(AddNewLocationActivity.this, HomeActivity.class));
                    finish();
                } else finish();
            }
        });

    }

    @Override
    public void onSuccessUpdateNestedLocation() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Common.isUpdateCurrentLocation || Common.isFirstTimeAddLocation) {
                    finish();
                    startActivity(new Intent(AddNewLocationActivity.this, HomeActivity.class));
                } else finish();
            }
        });
    }

    @Override
    public void onSuccessRemovedLocation() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
                Common.CURRENT_USER.getDeliveryAddressesList().remove(intent.getIntExtra(Constants.EXTRA_UPDATE,0));
            }
        });
    }

    @Override
    public void showProgressBar() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    @Override
    public void hideProgressBar() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.hide();
            }
        });

    }

    @Override
    public void showErrorMessage(String Message) {

    }

    @Override
    public void onSuccessMessage(String message) {

    }

    void showMessage(String Message) {
        Toasty.info(this, Message).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
//        } else {
//            if (mMap != null && isMapReady) {
//                mMap.setMyLocationEnabled(true);
//            }
//        }

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success)
                Log.e("Error", "Load Style Error");
        } catch (Resources.NotFoundException e) {
            Log.e("ERROR_MAP", "Resource not found " + e.getMessage());
        }

        mMap = googleMap;



//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.setMyLocationEnabled(true);
        //First zoom

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(AddNewLocationActivity.this, MapsActivity.class);
                intent.putExtra(Constants.EXTRA_LATITUDE, latitude);
                intent.putExtra(Constants.EXTRA_LONGITUDE, longitude);
                intent.putExtra("UPDATE", "UPDATE");
                startActivityForResult(intent, 1000);

                return true;

            }
        });

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
        LatLng latLng = new LatLng(latitude, longitude);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet("")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("ME"));

        mMap.setOnMapClickListener(latLng1 -> {

            Intent intent = new Intent(AddNewLocationActivity.this, MapsActivity.class);
            intent.putExtra(Constants.EXTRA_LATITUDE, latitude);
            intent.putExtra(Constants.EXTRA_LONGITUDE, longitude);
            intent.putExtra("UPDATE", "UPDATE");
            startActivityForResult(intent, 1000);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (requestCode == Constants.EXTRA_RESULT_CODE_CURRENT_LOCATION) {

                double latitudeIntent = data.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                double longitudeIntent = data.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);

                latitude = latitudeIntent;
                longitude = longitudeIntent;

                LatLng latLng = new LatLng(latitude, longitude);

                Log.d("HAZEM", "onActivityResult Update " + latitude + " " + longitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                marker.remove();
                marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet("")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("ME"));

            }


        } else {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_POSITION))
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu_delete:

                if (_id_Address.equalsIgnoreCase(Common.CURRENT_USER.getCurrentDeliveryAddress().getId()))
                {
                    Toasty.error(AddNewLocationActivity.this , "لا يمكن حذف العنوان الحالي يجب تغيره").show();
                }
                else
                presenter.removeItems(_id_Address);
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
