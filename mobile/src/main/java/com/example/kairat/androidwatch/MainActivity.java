package com.example.kairat.androidwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.IntentSender;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.location.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements DownloadResultReceiver.Receiver{

    private static int adventure_code= 5;
    private String myString;
    private boolean mIsInResolution;
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    //Toast toast;

    private String pickActivity;
    private double latitude;
    private double longitude;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call_intent();
    }


    private DownloadResultReceiver mReceiver;
    public void call_intent(){
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GetResultService.class);
/* Send optional extras to Download IntentService */
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);
        startService(intent);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case GetResultService.STATUS_RUNNING:
                setProgressBarIndeterminateVisibility(true);
                break;
            case GetResultService.STATUS_FINISHED:
                setProgressBarIndeterminateVisibility(false);
                latitude = Double.parseDouble(resultData.getString("myLat"));
                longitude = Double.parseDouble(resultData.getString("myLong"));
                break;
            case GetResultService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public void setFood(View view) {
        pickActivity = "food";
        //toast = Toast.makeText(getApplicationContext(), "Food picked!", Toast.LENGTH_SHORT);
        //toast.show();
        startNext();
    }

    public void setPark(View view) {
        pickActivity = "park|amusement_park";
        //toast = Toast.makeText(getApplicationContext(), "Parks picked!", Toast.LENGTH_SHORT);
        //toast.show();
        startNext();
    }

    public void setMuseum(View view) {
        pickActivity = "museum";
        //toast = Toast.makeText(getApplicationContext(), "Museums picked!", Toast.LENGTH_SHORT);
        //toast.show();
        startNext();

    }

    public void setShopping(View view) {
        pickActivity = "store|shopping_mall|department_store";
        //toast = Toast.makeText(getApplicationContext(), "Shopping picked!", Toast.LENGTH_SHORT);
        //toast.show();
        startNext();
    }

    //Uses a random number generator to pick an activity to do
    public void randomAct(View view) {
        Random rg = new Random();
        int ri = rg.nextInt(4);
        switch (ri) {
            case 0:
                pickActivity = "food";
                break;
            case 1:
                pickActivity = "store|shopping_mall|department_store";
                break;
            case 2:
                pickActivity = "museum";
                break;
            case 3:
                pickActivity = "park|amusement_park";
                break;
        }
        //toast = Toast.makeText(getApplicationContext(), "Let's go!", Toast.LENGTH_SHORT);
        //toast.show();
        startNext();
    }

    //Start Next competition
    public void startNext() {
        Intent i = new Intent(this, ResultActivity.class);
        myString = pickActivity+":"+latitude+":"+longitude;
        i.putExtra("qString", myString);
        startActivityForResult(i, adventure_code);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


