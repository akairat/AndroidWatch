package com.example.kairat.androidwatch;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.location.*;
import java.util.Calendar;
import java.util.Random;

import android.widget.EditText;

/*
* JSON Format for JSONProcess Class "hour:min:activity:latitude:longitude"
* TODO: Decide on 4 Activities to have [icons]
* TODO: Decide on what the Watch can send to phone
* TODO: Add a general menu to the help menu class to display
* TODO: Fill in JSONProcess class with parsing method in the oncreate portion [reference animal sounds app]
* */

public class MainActivity extends ActionBarActivity {

    private static int help_code = 3;
    private static int adventure_code= 5;
    private static int random_code= 6;
    private String myString;

    private int startHour;
    private int startMinute;
    private String pickActivity;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    //Currently Location is being fudged - When null, will always give 32-144 geocode
    //TODO: Create location service
    public void getLocation(View view) {
        // Get the location manager
        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch (NullPointerException e) {
            latitude = 42.361648260887;
            longitude = -71.0905194348;
        }
        System.out.println(latitude + "," + longitude);
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
                pickActivity = "shopping";
                break;
            case 1:
                pickActivity = "dining";
                break;
            case 2:
                pickActivity = "shopping";
            case 3:
                pickActivity = "nature";
        }
        System.out.println(pickActivity);
        //System DEBUG: Prints randomly selected activity
    }

    //Uses the user's selections to start the next activity
    //With regards to the watch: time picker + voice activation will be used to pick items
        //pressing "go" on the watch will pass in appropriate JSON info the JSONProcess class
    public void startNext(View view) {
        Intent i = new Intent(this, JSONProcess.class);
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
}


