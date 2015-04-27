package com.example.kairat.androidwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.location.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ListenerService extends WearableListenerService implements DownloadResultReceiver.Receiver {

    private boolean mIsInResolution;
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;


    private String pickActivity;
    private double latitude;
    private double longitude;

    private static final String TAG = "MOBILE MESSAGE: ";
    String PlaceType;

    int i =0;
    private String LOG_MESSAGE = "WebAPIExample";

    List<String> suggested_place_name;
    List<String> suggested_place_address;
    List<String> suggested_place_geo;
    List<String> suggested_place_distance;
    List<String> suggested_place_duration;

    JSONArray place_details = null;
    JSONArray place_list= null;

    private String PlaceLocation;

    private DownloadResultReceiver mReceiver;
    public void call_intent(){
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GetResultService.class);

/* Send optional extras to Download IntentService */
        intent.putExtra("PlaceType",PlaceType);
        intent.putExtra("PlaceLocation", PlaceLocation);

        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case GetResultService.STATUS_RUNNING:

                break;
            case GetResultService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */

                String[] place_name = resultData.getStringArray("place_name");
                String[] place_address = resultData.getStringArray("place_address");
                String[] place_geo = resultData.getStringArray("place_geo");
                String[] place_distance = resultData.getStringArray("place_distance");
                String[] place_duration = resultData.getStringArray("place_duration");
                Log.i(LOG_MESSAGE, " PlacesInfo" + place_name);
                Log.i(LOG_MESSAGE, " PlacesInfo" + place_address);
                Log.i(LOG_MESSAGE, " PlacesInfo" + place_geo);
                Log.i(LOG_MESSAGE, " PlacesInfo" + place_distance);
                Log.i(LOG_MESSAGE, " PlacesInfo" + place_duration);
                suggested_place_name = Arrays.asList(place_name);
                suggested_place_address= Arrays.asList(place_address);
                suggested_place_geo = Arrays.asList(place_geo);
                suggested_place_distance = Arrays.asList(place_distance);
                suggested_place_duration = Arrays.asList(place_duration);


                break;
            case GetResultService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received");
        PlaceType = messageEvent.getPath();
        PlaceLocation = "42.3613154,-71.0912821";

        call_intent();
    }

}
