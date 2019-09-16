package com.corptia.bringero.view.MapWork;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Comparator;

public class MyLatLng {
    private double myLocationLatitude, myLocationLongitude, pointLatitude, pointLongitude, distance;
    private LatLng latLng;
    private String imageUrl, title;
    ArrayList<String> mOrderIds;

    public MyLatLng(LatLng latLng, double lattitude, double longitude) {
        pointLatitude = latLng.latitude;
        pointLongitude = latLng.longitude;
        myLocationLatitude = lattitude;
        myLocationLongitude = longitude;
        this.latLng = latLng;
    }

    public ArrayList<String> getmOrderIds() {
        return mOrderIds;
    }

    public void setmOrderIds(ArrayList<String> mOrderIds) {
        this.mOrderIds = mOrderIds;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getmLatLng() {
        return latLng;
    }

    public Double getDistance() {
        Double latDiff, longDiff, squareLat, squareLong, squareSum;
        latDiff = myLocationLatitude - pointLatitude;
        longDiff = myLocationLongitude - pointLongitude;

        squareLat = latDiff * latDiff;
        squareLong = longDiff * longDiff;
        squareSum = squareLat + squareLong;
        distance = Math.sqrt(squareSum);

        return Math.sqrt(squareSum);
    }

    public double longitude() {
        return this.pointLongitude;
    }

    public double latitude() {
        return this.pointLatitude;
    }

    private Comparator<MyLatLng> compareDistance() {
        return new Comparator<MyLatLng>() {
            @Override
            public int compare(MyLatLng c1, MyLatLng c2) {
                return Double.compare(c1.getDistance(), c2.getDistance());
            }
        };
    }
}
