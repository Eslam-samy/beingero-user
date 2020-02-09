package com.corptia.bringero.ui.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.bumptech.glide.Glide;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Common.Constants;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.graphql.TripQuery;
import com.corptia.bringero.type.DeliveryOrderStatus;
import com.corptia.bringero.type.TrackingTripFilterInput;
import com.corptia.bringero.utils.PicassoMarker;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.logicbeanzs.uberpolylineanimation.MapAnimator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.corptia.bringero.Common.Constants.MAPVIEW_BUNDLE_KEY;

public class TrackingActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapsActivity";

    @BindView(R.id.mapView)
    MapView mMapView;

    private HashMap<String, Marker> mMarkers = new HashMap<>();


    //Play Service Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RESULATION_REQUEST = 300193;

    private GoogleMap mMap;
    private GeoApiContext mGeoApiContext;

    //For Zoom
    private LatLngBounds mMapBoundary;

    List<LatLng> listOfTracks;
    Location mLocation;

    String orderId, pilotId;

    //For set Zoom in center
    private ScaleGestureDetector gestureDetector;
    private int fingers = 0;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private Handler handler = new Handler();

    //For Get Location Pilot
    final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("MyLocation");
    final GeoFire geoFire = new GeoFire(dbRef);
    Marker pilotMarker;

    //TackTrip
    List<LatLng> latLngs = new ArrayList<>();

    private boolean isMarkerRotating = false;

    //Test
    private float start_rotation;
    private double[] latLng = new double[2];
    LatLng driverLatLng;
    private PicassoMarker marker;


    @BindView(R.id.btn_satellite)
    View btn_satellite;
    @BindView(R.id.btn_currentLocation)
    View btn_currentLocation;

    @BindView(R.id.img_pilot)
    CircleImageView img_pilot;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_rating)
    TextView txt_rating;
    @BindView(R.id.txt_order_id)
    TextView txt_order_id;
    @BindView(R.id.txt_arrived_in)
    TextView txt_arrived_in;
    @BindView(R.id.btn_back)
    Button btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        initView();

        initGoogleMap(savedInstanceState);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_satellite.setTag("MAP_TYPE_NORMAL");
        btn_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_satellite.getTag().equals("MAP_TYPE_SATELLITE")) {
                    btn_satellite.setTag("MAP_TYPE_NORMAL");
                    mMap.setMapType(mMap.MAP_TYPE_NORMAL);
                    btn_satellite.setBackgroundResource(R.drawable.layout_btn_satellite);
                } else {
                    mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
                    btn_satellite.setTag("MAP_TYPE_SATELLITE");
                    btn_satellite.setBackgroundResource(R.drawable.layout_btn_satellite_active);

                }
                // Here is where you set the map type

            }
        });
    }

//


    private void setMarker(DataSnapshot dataSnapshot) {

        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }


    void getLiveLocationPilot(String idPilot) {

        dbRef.child(idPilot).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                geoFire.getLocation(idPilot, new LocationCallback() {
                    @Override
                    public void onLocationResult(String key, GeoLocation location) {

//                        if (pilotMarker != null)
//                            pilotMarker.remove();
//
//                        pilotMarker = mMap.addMarker(new MarkerOptions().title("الطياااار").position(new LatLng(location.latitude, location.longitude)));

                        if (pilotMarker == null) {
                            pilotMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.latitude, location.longitude))
                                    .icon(bitmapDescriptorFromVector(TrackingActivity.this, R.drawable.ic_pilot))
                                    .title("الطيار"));
                        }

                        latLng[0] = location.latitude;
                        latLng[1] = location.longitude;

                        if (marker == null) {
//                            marker = new PicassoMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(latLng[0], latLng[1]))));
//                            Picasso.get().load(R.drawable.ic_pilot).resize( 50,  50)
//                                    .into(marker);

//                            pilotMarker = mMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(latLng[0], latLng[1]))
//                                    .icon(bitmapDescriptorFromVector(
//                                            TrackingActivity.this,
//                                            R.drawable.ic_pilot))
//                                    .title("الطيار بيه"));

//                            googleMapHomeFrag.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng[0], latLng[1]), 12.0f));
                        }

                        if ((latLng[0] != -1 && latLng[0] != 0) && (latLng[1] != -1 && latLng[1] != 0)) {
                            //googleMapHomeFrag.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng, 12.0f));
                            //float bearing = (float) bearingBetweenLocations(driverLatLng, new LatLng(location.getLatitude(), location.getLongitude()));
                            if (pilotMarker != null) {
                                moveVechile(pilotMarker, location);
                                rotateMarker(pilotMarker, 1f, start_rotation);

                                //UpdateLine
                                updateLine(pilotMarker);

                            }
                            driverLatLng = new LatLng(latLng[0], latLng[1]);
                        } else {

                        }


//                        rotateMarker(pilotMarker,bearingBetweenLocations(,1f));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updateLine(Marker pilotMarker) {
        LatLng latLng = pilotMarker.getPosition();
//        MapAnimator.getInstance().animateRoute(mMap, latLngs);


        int ixLastPoint = 0;
        for (int i = 0; i < latLngs.size(); i++) {
            LatLng point1 = latLngs.get(i);
            LatLng point2 = latLngs.get(i);
            List<LatLng> currentSegment = new ArrayList<>();
            currentSegment.add(point1);
            currentSegment.add(point2);
            if (PolyUtil.isLocationOnPath(latLng, currentSegment, true, 50)) {
                // save index of last point and exit loop
                ixLastPoint = i;
                Log.d("HAZEM" , "ixLastPoint " + ixLastPoint + " ---- " + latLngs.get(i).latitude + " --- " +  latLngs.get(i).longitude);

                Log.d("HAZEM" , "SIZE Befor : " + latLngs.size());
                latLngs.subList(0,i).clear();
                Log.d("HAZEM" , "SIZE After : " + latLngs.size());
//                mMap.clear();
                MapAnimator.getInstance().animateRoute(mMap, latLngs);



                break;
            }
            else {
                Log.d("HAZEM" , "facke");
            }
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initView() {

        ButterKnife.bind(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        mMap.setMyLocationEnabled(true);

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
            if (!success)
                Log.e("Error", "Load Style Error");
        } catch (Resources.NotFoundException e) {
            Log.e("ERROR_MAP", "Resource not found " + e.getMessage());
        }




        gestureDetector = new ScaleGestureDetector(TrackingActivity.this, new ScaleGestureDetector.OnScaleGestureListener() {
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


        getTrip(getIntent().getStringExtra(Constants.EXTRA_ORDER_ID));

    }


    private void initGoogleMap(Bundle savedInstanceState) {


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
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


    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    public Bitmap createCustomMarker(Context context, String avatar) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_marker, null);

        CircleImageView markerImage = marker.findViewById(R.id.img_profile);
//        TextView txt_name = marker.findViewById(R.id.txt_name);

        if (avatar != null && !avatar.isEmpty())
            Glide.with(TrackingActivity.this)
                    .load(avatar)
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(markerImage);
//            PicassoUtils.setImage(avatar, markerImage);
//        txt_name.setText(name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }


    public void moveVechile(final Marker myMarker, final GeoLocation finalPosition) {

        final LatLng startPosition = myMarker.getPosition();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + (finalPosition.latitude) * t,
                        startPosition.longitude * (1 - t) + (finalPosition.longitude) * t);
                myMarker.setPosition(currentPosition);
                // myMarker.setRotation(finalPosition.getBearing());


                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                    // handler.postDelayed(this, 100);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
            }
        });


    }


    public void rotateMarker(final Marker marker, final float toRotation, final float st) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
        final long duration = 1555;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;


                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                start_rotation = -rot > 180 ? rot / 2 : rot;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }



    //Get Data Trip
    private void getTrip(String deliveryOrderId){

        TrackingTripFilterInput trackingTripFilterInput = TrackingTripFilterInput.builder().deliveryOrderId(deliveryOrderId).build();
        MyApolloClient.getApollowClientAuthorization()
                .query(TripQuery.builder().filter(trackingTripFilterInput).build())
                .enqueue(new ApolloCall.Callback<TripQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<TripQuery.Data> response) {


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (response.data().TrackingTripQuery().getOne().status() == 200){

                                    TripQuery.@Nullable Data1 data = response.data().TrackingTripQuery().getOne().data();

                                    List<TripQuery.AvailableTrack> realTrack = data.availableTracks().get(data.availableTracks().size()-1);


                                    if (getIntent() != null && !response.data().TrackingTripQuery().getOne().data().DeliveryOrderResponse().DeliveryOrderResponseData().status().rawValue().equalsIgnoreCase(DeliveryOrderStatus.DELIVERED.rawValue())) {
                                        getLiveLocationPilot(getIntent().getStringExtra(Constants.EXTRA_PILOT_ID));
                                    }


                                    latLngs = new ArrayList<>();
                                    for (TripQuery.AvailableTrack latLng : realTrack) {
                                        latLngs.add(new LatLng(latLng.lat(), latLng.lng()));
                                    }


                                    //Create Line
                                    MapAnimator.getInstance().animateRoute(mMap, latLngs);
                                    zoomRoute(latLngs);

                                    if (Common.CURRENT_USER != null) {
                                        String avatar = Common.CURRENT_USER.getAvatarName();

                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latLngs.get(latLngs.size() - 1).latitude, latLngs.get(latLngs.size() - 1).longitude))
                                                .icon(BitmapDescriptorFactory
                                                        .fromBitmap(
                                                                createCustomMarker(
                                                                        TrackingActivity.this
                                                                        , Common.BASE_URL_IMAGE + avatar))))
                                                .setTitle(Common.CURRENT_USER.getFullName());
                                    }


                                }
                                else
                                {



                                }

                            }
                        });



                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

    }

}
