package com.corptia.bringero.view.location.addNewLocation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.model.FlatTypeModel;
import com.corptia.bringero.type.FlatType;
import com.corptia.bringero.view.MapWork.MapsActivity;
import com.corptia.bringero.view.location.deliveryLocation.SelectDeliveryLocationPresenter;
import com.corptia.bringero.view.location.deliveryLocation.SelectDeliveryLocationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

public class AddNewLocationActivity extends AppCompatActivity implements SelectDeliveryLocationView , OnMapReadyCallback {

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
    FlatTypeModel flatTypeModel ;

    //For Map
    private GoogleMap mMap;
    Marker marker;



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

            if (region.trim().equals("") || region.isEmpty()) {
                showMessage("region is required");
                return;
            } else if (address_name.trim().equals("") || address_name.isEmpty()) {
                showMessage("name is required");
                return;
            } else if (building.trim().equals("") || building.isEmpty()) {
                showMessage("building is required");
                return;
            } else if (street.trim().equals("") || street.isEmpty()) {
                showMessage("street is required");
                return;
            } else if (floor.trim().equals("") || floor.isEmpty()) {
                showMessage("floor is required");
                return;
            } else if (flat.trim().equals("") || flat.isEmpty()) {
                showMessage("flat is required");
                return;
            }

            presenter.addNewAddress(address_name,
                    region,
                    street,
                    flatTypeModel.getFlatType(),
                    Integer.parseInt(floor),
                    Integer.parseInt(flat),
                    Integer.parseInt(building),
                    latitude,
                    longitude);

        });

        fillSpinnerLanguage();

        spinner_flatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                flatTypeModel = (FlatTypeModel) parent.getSelectedItem();
                // Log.d("onItemSelected", "" + language.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void init() {

        intent = getIntent();
        if (intent != null) {
            latitude = intent.getDoubleExtra("latitude", 0);
            longitude = intent.getDoubleExtra("longitude", 0);

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

        finish();

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

        mMap = googleMap;

//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.getUiSettings().setAllGesturesEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.setMyLocationEnabled(true);
        //First zoom

        mMap.moveCamera(CameraUpdateFactory.zoomTo(13f));
        LatLng latLng = new LatLng(latitude , longitude);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet("")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("ME"));

        mMap.setOnMapClickListener(latLng1 -> {

            Intent intent = new Intent(AddNewLocationActivity.this , MapsActivity.class);
            intent.putExtra(Constants.EXTRA_LATITUDE, latitude);
            intent.putExtra(Constants.EXTRA_LONGITUDE, longitude);
            intent.putExtra("UPDATE", "UPDATE");
            startActivityForResult(intent , 1000);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

            if (data.hasExtra("UPDATE")) {

                double latitudeIntent = data.getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                double longitudeIntent = data.getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);

                latitude = latitudeIntent;
                longitude = longitudeIntent;

                LatLng latLng = new LatLng(latitude , longitude);

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
}
