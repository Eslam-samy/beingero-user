package com.corptia.bringero.ui.MapWork;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.util.StringUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.ui.location.addNewLocation.AddNewLocationActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private int failedTimes;
    private Boolean isMapReady;
    Location currentLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Marker marker;

//    AutoCompleteTextView searchAutoComplete;
//    List<String> dataPlacesAutoComplete;
//    List<String> placesIDS;
//    AutoCompleteAdapter adapter;
//    public static ArrayList<MyLatLng> listPoints;

    Boolean firstTime;

    //Toolbar toolbar;

    //This Local Data Will Store After Click On Map To get Tour Location
    double latitude, longitude, newLatitude, newLongitude;
    String address = "";

    @BindView(R.id.saveLocationBtn)
    Button saveLocationBtn;

    LocationCallback locationCallback;


    //For set Zoom in center
    private ScaleGestureDetector gestureDetector;
    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private Handler handler = new Handler();


    //For AutoComplete
    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS);

    AutocompleteSupportFragment place_fragment;


    //tools Control
    @BindView(R.id.btn_satellite)
    View btn_satellite;
    @BindView(R.id.btn_currentLocation)
    View btn_currentLocation;
    @BindView(R.id.enableLocation)
    Button enableLocation;
    @BindView(R.id.enableLocationLO)
    ConstraintLayout enableLocationLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);
        btn_satellite.setTag("MAP_TYPE_NORMAL");


        //For AutoComplete
        initPlaces();
        setupPlaceAutoComplete();

        firstTime = true;
        //saveLocationBtn.setVisibility(View.GONE);

//        dataPlacesAutoComplete = new ArrayList<>();
//        placesIDS = new ArrayList<>();

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        failedTimes = 0;
        isMapReady = false;
        //build the googleApiClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (!getIntent().hasExtra("FACILITY")) {
            mLocationRequest.setInterval(5000);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            if (mMap != null && isMapReady) {
                mMap.setMyLocationEnabled(true);
            }
        }
//        searchAutoComplete = findViewById(R.id.searchAutoComplete);
//        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, dataPlacesAutoComplete);
//        searchAutoComplete.setAdapter(adapter);


//        searchAutoComplete.removeTextChangedListener(this);
//        searchAutoComplete.setText("");
//        searchAutoComplete.addTextChangedListener(this);

//        searchAutoComplete.setHint(getString(R.string.search_hint_location));
//        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String placeId = placesIDS.get(position);
//                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + getString(R.string.google_maps_key);
//                TaskRequestPlaceDetails taskRequestPlaceDetails = new TaskRequestPlaceDetails();
//                taskRequestPlaceDetails.execute(url);
//
//            }
//        });


        //Event

        btn_currentLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Location mLocation = getLastBestLocation();
                if (mLocation != null) {
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 17);
                    mMap.animateCamera(yourLocation);
                }

            }
        });

        btn_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_satellite.getTag().equals("MAP_TYPE_NORMAL")) {
                    mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                    btn_satellite.setTag("MAP_TYPE_SATELLITE");
                } else {
                    mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                    btn_satellite.setTag("MAP_TYPE_NORMAL");
                }
            }
        });

        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float[] results = new float[1];
                Location.distanceBetween(31.440350, 31.683745,
                        newLatitude, newLongitude, results);
                Log.d("HAZEM", "HI : " + results[0]);

                double radiusInMeters = 7.0 * 1000.0; //1 KM = 1000 Meter

                if (results[0] > radiusInMeters) {

                    Log.d("HAZEM", " Outside, distance from center " + getCompleteAddressString(newLatitude, newLongitude));
                    Toasty.info(MapsActivity.this, "Out of bounds Damietta El-Gadeeda City").show();

//                    Toast.makeText(getBaseContext(),
//                            "Outside, distance from center: " + results[0] + " radius: " + radiusInMeters,
//                            Toast.LENGTH_LONG).show();

                } else {

                    saveLocation();

                    Log.d("HAZEM", " Inside, distance from center " + getCompleteAddressString(newLatitude, newLongitude));

//                    Toast.makeText(getBaseContext(),
//                            "Inside, distance from center: " + results[0] + " radius: " + radiusInMeters ,
//                            Toast.LENGTH_LONG).show();

                }


            }
        });

    }

    private void setupPlaceAutoComplete() {

        place_fragment = (AutocompleteSupportFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.places_autocomplete_fragment);


        place_fragment.setPlaceFields(placeFields);
        place_fragment.setCountry("EG");


        RectangularBounds bounds = RectangularBounds.newInstance
                (new LatLng(31.394314462365035, 31.609457621727913),
                        new LatLng(31.480434888055356, 31.776484141503303));
//        place_fragment.setLocationBias(bounds);
        place_fragment.setLocationRestriction(bounds);

//        place_fragment.setTypeFilter(TypeFilter.CITIES);

        place_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                Log.d("HAZEM", "getName : " + place.getName());
                Log.d("HAZEM", "getAddress : " + place.getAddress());
                Log.d("HAZEM", "getLatLng : " + place.getLatLng());
                Log.d("HAZEM", "getLatLng : " + place.getId());

                if (place.getLatLng() != null) {
                    LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    mMap.animateCamera(yourLocation);
                }

            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d("HAZEM", "" + status.getStatusMessage());

            }
        });


    }

    private void initPlaces() {

        Places.initialize(this, getString(R.string.google_maps_api_key));
        placesClient = Places.createClient(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success)
                Log.e("Error", "Load Style Error");
        } catch (Resources.NotFoundException e) {
            Log.e("ERROR_MAP", "Resource not found " + e.getMessage());
        }

        isMapReady = true;
        mMap = googleMap;
        gestureDetector = new ScaleGestureDetector(MapsActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= 50) {
                    lastZoomTime = detector.getEventTime();
                    googleMap.animateCamera(CameraUpdateFactory.zoomBy(getZoomValue(detector.getCurrentSpan(), lastSpan)), 50, null);
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                lastSpan = -1;

            }
        });
        //        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                latitude = currentLocation.getLatitude();
//                longitude = currentLocation.getLongitude();
//                LatLng myLatLng = new LatLng(latitude, longitude);
//
//                if (getCompleteAddressString(latitude, longitude).indexOf("Unnamed") == -1) {
//                    address = getCompleteAddressString(latitude, longitude);
//                } else {
//                    address = getCompleteAddressString(latitude, longitude).substring(13);
//                }
//                mMap.clear();
//                if (marker != null) {
//                    marker.remove();
//                }
//                /*marker = mMap.addMarker(new MarkerOptions()
//                        .position(myLatLng)
//                        .snippet("")
//                        .icon(BitmapDescriptorFactory
//                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                        .title("ME"));*/
//
//                saveLocationBtn.setVisibility(View.VISIBLE);
//                // saveLocationBtn.setText(getString(R.string.save_location) + "\n" + address);
//                return false;
//            }
//        });

//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                //TODO create a toast to tell the user to click longer on the map
//                Toasty.info(MapsActivity.this, getString(R.string.click_longer_to_set_the_place)).show();
//            }
//        });
//
//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                mMap.clear();
//                newLatitude = latLng.latitude;
//                newLongitude = latLng.longitude;
//                if (getCompleteAddressString(newLatitude, newLongitude).indexOf("Unnamed") == -1) {
//                    newAddress = getCompleteAddressString(newLatitude, newLongitude);
//                } else {
//                    newAddress = getCompleteAddressString(newLatitude, newLongitude).substring(13);
//                }
//                saveLocationBtn.setVisibility(View.VISIBLE);
//
//                saveLocationBtn.setText(getString(R.string.save_location) + "\n" + newAddress);
////                    if(getCompleteAddressString(latLng.latitude , latLng.longitude).contains("Unnamed"));
////                    Log.w("asd","YEEEEEEEEEEES");
//
//                //address = getCompleteAddressString(latLng.latitude , latLng.longitude) ;
//                if (marker != null) {
//                    marker.remove();
//                }
//                marker = mMap.addMarker(new MarkerOptions()
//                        .position(latLng)
//                        .snippet("")
//                        .icon(BitmapDescriptorFactory
//                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                        .title("ME"));
//            }
//
//        });


//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                // Cleaning all the markers.
//                if (mMap != null) {
//                    mMap.clear();
//                }
//
//                mPosition = mMap.getCameraPosition().target;
//                mZoom = mMap.getCameraPosition().zoom;
//
//                if (mTimerIsRunning) {
//                    mDragTimer.cancel();
//                }
//
//            }
//        });
    }


    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            enableLocationLO.setVisibility(View.VISIBLE);
            saveLocationBtn.setVisibility(View.GONE);

            enableLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
        } else {
            afterAccessLocation();
        }
    }

    private void afterAccessLocation() {
        if (mMap != null && isMapReady) {
            mMap.setMyLocationEnabled(true);
        }

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    currentLocation = locationResult.getLastLocation();

                    double newlatitude;
                    double newlongitude;
                    if (getIntent().hasExtra("UPDATE")) {

                        newlatitude = getIntent().getDoubleExtra(Constants.EXTRA_LATITUDE, 0);
                        newlongitude = getIntent().getDoubleExtra(Constants.EXTRA_LONGITUDE, 0);

                        Log.d("HAZEM", "newlatitude " + newlatitude);
                        Log.d("HAZEM", "newlongitude " + newlongitude);
                    } else {
                        newlatitude = currentLocation.getLatitude();
                        newlongitude = currentLocation.getLongitude();
                    }


                    LatLng myLatLng = new LatLng(newlatitude, newlongitude);
                    if (firstTime) {
                        //zoom to my location
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                        //mMap.addMarker(new MarkerOptions().position(myLatLng));
                        firstTime = false;
                    }
                }
            }
        };

        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null);
        enableLocationLO.setVisibility(View.GONE);
        saveLocationBtn.setVisibility(View.VISIBLE);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.setMyLocationEnabled(true);
        //First zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
        if (currentLocation != null) {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

            if (getCompleteAddressString(latitude, longitude).indexOf("Unnamed") == -1) {
                address = getCompleteAddressString(latitude, longitude);
            } else {
                address = getCompleteAddressString(latitude, longitude).substring(13);
            }
//                    if(getCompleteAddressString(latLng.latitude , latLng.longitude).contains("Unnamed"));

            //address = getCompleteAddressString(latLng.latitude , latLng.longitude) ;

            //Log.d("latLnglatLng","Address : "+getCompleteAddressString(latLng.latitude , latLng.longitude));
            saveLocationBtn.setVisibility(View.VISIBLE);
            // saveLocationBtn.setText(getString(R.string.save_location) + "\n" + address);
            if (marker != null) {
                marker.remove();
            }

        }
        mMap.setOnCameraChangeListener(cameraPosition -> {
            Log.d("HAZEM", " Hello I am here " + cameraPosition.target.latitude + " ------ " + cameraPosition.target.longitude);
            newLatitude = cameraPosition.target.latitude;
            newLongitude = cameraPosition.target.longitude;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        afterAccessLocation();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    ToastUtil.showCustomToast(MapsActivity.this,
                            getString(R.string.permission_denied),
                            404,
                            getDrawable(R.drawable.ic_check_white_24dp),
                            getResources().getColor(R.color.colorAccent),
                            getResources().getColor(R.color.white));
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public void saveLocation() {

        if (getIntent() != null) {
            if (getIntent().hasExtra("UPDATE")) {

                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_LATITUDE, newLatitude);
                intent.putExtra(Constants.EXTRA_LONGITUDE, newLongitude);
                //intent.putExtra("longitude", true);

                //Log.d("HAZEM" , "This From Update : " + newLatitude + " - " + newLongitude);

                setResult(Constants.EXTRA_RESULT_CODE_CURRENT_LOCATION, intent);
                finish();

            } else {
                Intent intent = new Intent(this, AddNewLocationActivity.class);
                intent.putExtra(Constants.EXTRA_LATITUDE, newLatitude);
                intent.putExtra(Constants.EXTRA_LONGITUDE, newLongitude);
                startActivity(intent);
                finish();


                //Log.d("HAZEM" , "First Time : " + newLatitude + " - " + newLongitude);

            }

        }


//        if (!newAddress.equals("")) {
//            intent.putExtra("address", newAddress);
//        } else if (!searchAddress.equals("")) {
//            intent.putExtra("address", searchAddress);
//        } else {
//            intent.putExtra("address", address);
//        }
//
//        if (newLatitude != 0) {
//            intent.putExtra("latitude", newLatitude);
//        } else if (latLng != null) {
//            if (latLng.latitude != 0) intent.putExtra("latitude", latLng.latitude);
//        } else {
//            intent.putExtra("latitude", latitude);
//        }
//
//        if (newLongitude != 0) {
//            intent.putExtra("longitude", newLongitude);
//        } else if (latLng != null) {
//            if (latLng.longitude != 0) intent.putExtra("longitude", latLng.longitude);
//        } else {
//            intent.putExtra("longitude", longitude);
//        }


    }

    //When Back Arrow
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (locationCallback != null)
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }


    ///-------- For Zoom Center -----------
    private float getZoomValue(float currentSpan, float lastSpan) {
        double value = (Math.log(currentSpan / lastSpan) / Math.log(1.55d));
        return (float) value;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_UP:
                fingers = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                break;
        }
        if (fingers > 1) {
            disableScrolling();
        } else if (fingers < 1) {
            enableScrolling();
        }
        if (fingers > 1) {
            return gestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    private void enableScrolling() {
        if (mMap != null && !mMap.getUiSettings().isScrollGesturesEnabled()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                }
            }, 50);
        }
    }

    private void disableScrolling() {
        handler.removeCallbacksAndMessages(null);
        if (mMap != null && mMap.getUiSettings().isScrollGesturesEnabled()) {
            mMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    //------------------ End ------------------


    /*

                JSONObject jsonResponse = new JSONObject(s);
                JSONObject result = jsonResponse.optJSONObject("result");
                JSONObject geometry = result.optJSONObject("geometry");
                JSONObject location = geometry.optJSONObject("location");
                String latSt = location.optString("lat");
                String longSt = location.optString("lng");
                double lat = Double.valueOf(latSt);
                double lng = Double.valueOf(longSt);
                latLng = new LatLng(lat, lng);
                newAddress = getCompleteAddressString(lat, lng);
                String icon = result.optString("icon");
                saveLocationBtn.setVisibility(View.VISIBLE);
                //saveLocationBtn.setText(getString(R.string.save_location) + "\n" + newAddress);
                URL url = new URL(icon);
                name = result.optString("firstName");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.clear();
                TaskGetIcon taskGetIcon = new TaskGetIcon();
                taskGetIcon.execute(url);

     */


    @RequiresApi(api = Build.VERSION_CODES.M)
    private Location getLastBestLocation() {

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return null;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }


}
