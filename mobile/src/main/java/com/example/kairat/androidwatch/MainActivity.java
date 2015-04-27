package com.example.kairat.androidwatch;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;
import java.util.Random;

/*
* JSON Format for JSONProcess Class "hour:min:activity:latitude:longitude"
* TODO: Decide on 4 Activities to have [icons]
* TODO: Decide on what the Watch can send to phone
* TODO: Add a general menu to the help menu class to display
* TODO: Fill in JSONProcess class with parsing method in the oncreate portion [reference animal sounds app]
* */

public class MainActivity extends ActionBarActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static int help_code = 3;
    private static int adventure_code= 5;
    private static int random_code= 6;
    private String myString;
    private boolean mIsInResolution;
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private int startHour;
    private int startMinute;
    private String pickActivity;
    private double latitude;
    private double longitude;

    private Location mLastLocation;
    private Location mCurrentLocation;
    private double mLat;
    private double mLong;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    public void setStartTime(int hourOfDay, int minute) {
        startHour = hourOfDay;
        startMinute = minute;
    }

    public void clickTime(View view) {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                setStartTime(selectedHour,selectedMinute);
                //System.out.println(startHour + " " + startMinute);
                //DEBUG statement above: Basically prints to console the time in 24 hour format
            }
        }, hour, minute, false);//No to 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        Context context = getApplicationContext();
        CharSequence text = "Setting time";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Currently Location is being fudged - When null, will always give 32-144 geocode
    //TODO: Create location service
    public void getLocation(View view) {
        // Get the location manager
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
        else {
            System.out.println("Location was not obtained.");
        }
        /*try {
          createLocationRequest();
        } catch (Exception e) {
            latitude = 42.361648260887;
            longitude = -71.0905194348;
        }*/
        System.out.println(latitude + "," + longitude);
        Context context = getApplicationContext();
        CharSequence text = "Location Set!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void resetAll(View view) {
        //reset all checkboxes
    }
    //Starts an that gives general directions on application use
    public void helpMenu(View view) {
        Intent hm = new Intent(this, helpMenu.class);
        startActivityForResult(hm, help_code);
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
        System.out.println(pickActivity);
        Context context = getApplicationContext();
        CharSequence text = pickActivity +" picked!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        //System DEBUG: Prints randomly selected activity
    }

    //Uses the user's selections to start the next activity
    //With regards to the watch: time picker + voice activation will be used to pick items
        //pressing "go" on the watch will pass in appropriate JSON info the JSONProcess class
    public void startNext(View view) {
        Intent i = new Intent(this, ResultActivity.class);
        myString = startHour+":"+startMinute+":"+pickActivity+":"+latitude+":"+longitude;
        System.out.println(myString);
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

    @Override
    public void onConnected(Bundle bundle) {
        //Using GooglePlay Services Location API
        mLastLocation= LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLat = mLastLocation.getLatitude();
            mLong = mLastLocation.getLongitude();
        }
        System.out.println(mLat + "," + mLong);
        Context context = getApplicationContext();
        CharSequence text = "Location Set!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        System.out.println("GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.println("GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            System.out.println("Exception while starting resolution activity");
            e.printStackTrace();
            retryConnecting();
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }
}


