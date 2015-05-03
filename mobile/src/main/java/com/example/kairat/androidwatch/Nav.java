package com.example.kairat.androidwatch;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.graphics.*;
import android.provider.MediaStore.*;
import android.net.Uri;
import android.content.Intent;
import android.content.ContentValues;
import android.provider.MediaStore;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

public class Nav extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String place_Name;
    private double place_Lat;
    private double place_Long;
    private double user_Lat;
    private double user_Long;
    private Bitmap bitmap;

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
        String[] address = result_array[0].split(",");

        place_Lat = Double.parseDouble(address[0]);
        place_Long = Double.parseDouble(address[1]);
        user_Lat = Double.parseDouble(result_array[1]);
        user_Long = Double.parseDouble(result_array[2]);
        place_Name = result_array[3];
        setUpMapIfNeeded();
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        Button b = (Button) findViewById(R.id.button);
        b.setVisibility(View.VISIBLE);
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
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        LatLng latlng = new LatLng(user_Lat, user_Long);
        mMap.addMarker(new MarkerOptions().position(latlng).title("My Location"));
        LatLng mDestination = new LatLng(place_Lat, place_Long);
        mMap.addMarker(new MarkerOptions().position(mDestination).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(place_Name));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng avg = new LatLng((user_Lat+place_Lat)/(2.0), (user_Long+place_Long)/(2.0));

        /*LatLngBounds bounds = new LatLngBounds.Builder()
                .include(mDestination)
                .include(latlng)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 70));*/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(avg));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        // Will add in functionality to search for final destination
        // mMap.addMarker(new MarkerOptions().position(new LatLng(place_Lat, place_Long)).title("Destination"));
    }

    private String getMapsApiDirectionsUrl() {
        String origin = "origin=" + user_Lat + "," + user_Long;
        String destination = "destination=" + place_Lat + "," + place_Long;
        String sensor = "sensor=false";
        String params = origin + "&" + destination + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    //Takes screenshot and send to watch?
    public void sendWatch(View view) {
        SnapshotReadyCallback callback = new SnapshotReadyCallback()
        {
            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                OutputStream fout = null;
                String filePath = System.currentTimeMillis() + ".jpeg";
                try
                {
                    fout = openFileOutput(filePath,
                            MODE_WORLD_READABLE);
                    // Write the string to the file
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                    fout.flush();
                    fout.close();
                }
                catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }
                openShareImageDialog(filePath);
            }
        };
        mMap.snapshot(callback);
    }

    //TODO: CHANGE THIS TO SEND IMG MESSAGE TO WATCH
    public void openShareImageDialog(String filePath)
    {
        File file = this.getFileStreamPath(filePath);
        if(!filePath.equals(""))
        {
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
            startActivity(Intent.createChooser(intent, "Share Image"));
        }
        else
        {
           System.out.println("Failed :( ");
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    LatLng position = new LatLng(Double.parseDouble(point.get("lat")), Double.parseDouble(point.get("lng")));

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(30);
                polyLineOptions.color(Color.YELLOW);
            }

            mMap.addPolyline(polyLineOptions);
        }

    }

}
