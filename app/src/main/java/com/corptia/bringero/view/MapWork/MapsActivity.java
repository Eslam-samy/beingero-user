package com.corptia.bringero.view.MapWork;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.corptia.bringero.R;
import com.corptia.bringero.view.location.addNewLocation.AddNewLocationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private int failedTimes;
    private Boolean isMapReady;
    Location currentLocation;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Marker marker;
    AutoCompleteTextView searchAutoComplete;
    List<String> dataPlacesAutoComplete;
    List<String> placesIDS;
    AutoCompleteAdapter adapter;
    public static ArrayList<MyLatLng> listPoints;

    Boolean firstTime;
//    Toolbar toolbar;

    //This Local Data Will Store After Click On Map To get Tour Location
    double latitude, longitude, newLatitude, newLongitude;
    String address = "";
    String newAddress = "";
    String searchAddress = "";

    Button saveLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        firstTime = true;
        saveLocationBtn = findViewById(R.id.saveLocationBtn);
        //saveLocationBtn.setVisibility(View.GONE);

        dataPlacesAutoComplete = new ArrayList<>();
        placesIDS = new ArrayList<>();

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
        searchAutoComplete = findViewById(R.id.searchAutoComplete);
        adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line, android.R.id.text1, dataPlacesAutoComplete);
        searchAutoComplete.setAdapter(adapter);


        searchAutoComplete.removeTextChangedListener(this);
        searchAutoComplete.setText("");
        searchAutoComplete.addTextChangedListener(this);

        searchAutoComplete.setHint(getString(R.string.search_hint_location));
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String placeId = placesIDS.get(position);
                String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + getString(R.string.google_maps_key);
                TaskRequestPlaceDetails taskRequestPlaceDetails = new TaskRequestPlaceDetails();
                taskRequestPlaceDetails.execute(url);

            }
        });

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        } else {
            if (mMap != null && isMapReady) {
                mMap.setMyLocationEnabled(true);
            }
        }
        mMap = googleMap;
        isMapReady = true;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setMyLocationEnabled(true);
        //First zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13f));


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
            saveLocationBtn.setText(getString(R.string.save_location) + "\n" + address);
            if (marker != null) {
                marker.remove();
            }

        }
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
                LatLng myLatLng = new LatLng(latitude, longitude);

                if (getCompleteAddressString(latitude, longitude).indexOf("Unnamed") == -1) {
                    address = getCompleteAddressString(latitude, longitude);
                } else {
                    address = getCompleteAddressString(latitude, longitude).substring(13);
                }
                mMap.clear();
                if (marker != null) {
                    marker.remove();
                }
                /*marker = mMap.addMarker(new MarkerOptions()
                        .position(myLatLng)
                        .snippet("")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("ME"));*/

                saveLocationBtn.setVisibility(View.VISIBLE);
                saveLocationBtn.setText(getString(R.string.save_location) + "\n" + address);
                return false;
            }
        });

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

        mMap.setOnCameraChangeListener(cameraPosition -> {

            latitude = cameraPosition.target.latitude;
            longitude = cameraPosition.target.longitude;
        });
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            if (mMap != null && isMapReady) {
                mMap.setMyLocationEnabled(true);
            }
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult.getLastLocation() != null) {
                        currentLocation = locationResult.getLastLocation();
                        latitude = currentLocation.getLatitude();
                        longitude = currentLocation.getLongitude();
                        LatLng myLatLng = new LatLng(latitude, longitude);
                        if (firstTime) {
                            //zoom to my location\zoooooooooooooooom
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
                            //mMap.addMarker(new MarkerOptions().position(myLatLng));
                            firstTime = false;
                        }
                    }
                }
            }, null);
        }
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
                        mMap.setMyLocationEnabled(true);
                        if (mMap != null && isMapReady) {
                            mMap.setMyLocationEnabled(true);
                        }
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

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }//        Log.e(TAG, "requestDirection: " + responseString);

        return responseString;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String searchText = String.valueOf(s);
        searchPlaces(searchText);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    private void searchPlaces(String text) {
        //https://maps.googleapis.com/maps/api/place//
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key="
                + getString(R.string.google_maps_key) + "&input=" + "\"" + text + "\"";

        dataPlacesAutoComplete.clear();
        adapter.notifyDataSetChanged();
        TaskRequestPlaces taskRequestPlaces = new TaskRequestPlaces();
        taskRequestPlaces.execute(url);


    }

    public class TaskRequestPlaces extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            try {
                placesIDS.clear();
                adapter.clearData();
                adapter.notifyDataSetChanged();

                JSONObject jsonResponse = new JSONObject(s);
                JSONArray predictions = jsonResponse.optJSONArray("predictions");
                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject currentPrediction = predictions.optJSONObject(i);
                    JSONObject structured_formatting = currentPrediction.optJSONObject("structured_formatting");
                    String placeId = currentPrediction.optString("place_id");
                    placesIDS.add(placeId);

                    String main_text = structured_formatting.optString("main_text");
                    dataPlacesAutoComplete.add(main_text);
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class TaskRequestPlaceDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            try {
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
                saveLocationBtn.setText(getString(R.string.save_location) + "\n" + newAddress);
                URL url = new URL(icon);
                name = result.optString("firstName");
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.clear();
                TaskGetIcon taskGetIcon = new TaskGetIcon();
                taskGetIcon.execute(url);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public class TaskGetIcon extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... urls) {

            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeStream(urls[0].openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            markerOptions = new MarkerOptions();
            mBitmap = bitmap;
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mBitmap)).title(name).position(latLng);
            mMap.addMarker(markerOptions);
        }
    }

    Bitmap mBitmap;
    MarkerOptions markerOptions;
    String name;
    LatLng latLng;


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

    public void saveLocation(View view) {

        Intent intent = new Intent(this , AddNewLocationActivity.class);
        if (!newAddress.equals("")) {
            intent.putExtra("address", newAddress);
        } else if (!searchAddress.equals("")) {
            intent.putExtra("address", searchAddress);
        } else {
            intent.putExtra("address", address);
        }

        if (newLatitude != 0) {
            intent.putExtra("latitude", newLatitude);
        } else if (latLng != null) {
            if (latLng.latitude != 0) intent.putExtra("latitude", latLng.latitude);
        } else {
            intent.putExtra("latitude", latitude);
        }

        if (newLongitude != 0) {
            intent.putExtra("longitude", newLongitude);
        } else if (latLng != null) {
            if (latLng.longitude != 0) intent.putExtra("longitude", latLng.longitude);
        } else {
            intent.putExtra("longitude", longitude);
        }
        startActivity(intent);
        finish();

    }

    //When Back Arrow
    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return true;
    }

}
