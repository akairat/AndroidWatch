package com.example.kairat.androidwatch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

public class Nav extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String address;
    private double placelat;
    private double placelong;
    private double user_Lat;
    private double user_Long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        String aString = extras.getString("aString");
        String[] result_array = aString.split(":");
        address = result_array[0];
        //placelat = Double.parseDouble(result_array[1]);
        //placelong = Double.parseDouble(result_array[2]);
        user_Lat = Double.parseDouble(result_array[1]);
        user_Long = Double.parseDouble(result_array[2]);
        System.out.println("YEAYEAH"+user_Lat +","+ user_Long);
        //System.out.println("OKAYTEHH"+placelat +","+ placelong);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Set up a map with a marker for our location
     */
    private void setUpMap() {
        LatLng latlng = new LatLng(user_Lat, user_Long);
        mMap.addMarker(new MarkerOptions().position(latlng).title("Your Location"));
        //LatLng mDestination = new LatLng(placelat, placelong);
        //mMap.addMarker(new MarkerOptions().position(mDestination).title("Your Destination"));
        System.out.println("BOOM Marker");
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        System.out.println("Map Type Set");

        System.out.println(latlng + "OKAY THEN");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        System.out.println("Zoom");
        // Will add in functionality to search for final destination
        // mMap.addMarker(new MarkerOptions().position(new LatLng(place_Lat, place_Long)).title("Destination"));
    }
}
