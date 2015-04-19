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

import java.util.Calendar;
import java.util.Random;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewStub;

/*
* JSON Format for JSONProcess Class "time:activity:budgetMax"
* TODO: Decide on 4-6 Activities to have
* TODO: Decide on what the Watch can send to phone
* TODO: Add a general menu to the help menu class to display
* TODO: Fill in JSONProcess class
* */

public class MainActivity extends ActionBarActivity {

    private static int help_code = 3;
    private static int adventure_code= 5;
    private static int random_code= 6;
    private String myString;

    private TextView mTextView;
    private int startHour;
    private int startMinute;

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

    public void getLocation(View view) {
        //get GPS coordinates
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
        int ri = rg.nextInt(6);
        switch (ri) {
            case 0:
                //myString = "time:activity:activity:budgetMin:budgetMax"
        }
        Intent ra = new Intent(this, JSONProcess.class);
        startActivityForResult(ra, random_code);
    }

    //Uses the user's selections to start the next activity
    //With regards to the watch: time picker + voice activation will be used to pick items
        //pressing "go" on the watch will pass in appropriate JSON info the JSONProcess class
    public void startNext(View view) {
        Intent i = new Intent(this, JSONProcess.class);
        myString = "these:words:are:separated:by:colons";
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
