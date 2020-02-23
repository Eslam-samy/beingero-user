package com.corptia.bringero.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.corptia.bringero.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View view;

    private Context myContext;

    public InfoWindowAdapter(Context aContext) {
        this.myContext = aContext;

        LayoutInflater inflater = (LayoutInflater) myContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.layout_custom_marker_title,
                null);
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (marker != null
                && marker.isInfoWindowShown() && !marker.getTitle().isEmpty()) {
//            marker.hideInfoWindow();
            marker.showInfoWindow();
            displayView(marker);
        }
        return null;
    }

    private void displayView(final Marker marker) {
        ((TextView)view.findViewById(R.id.snippet)).setText(marker.getTitle());
    }

    @Override
    public View getInfoWindow(final Marker marker) {

        final String title = marker.getTitle();
        final TextView snippetUi =  view
                .findViewById(R.id.snippet);

        if (title != null && !title.isEmpty()) {

            snippetUi.setText(title);
        } else {
            return null;
        }

        return view;
    }

//    @Override
//    public View getInfoWindow(Marker marker) {
//        return null;
//    }
}