package com.example.kairat.androidwatch;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class JSONProcess extends ActionBarActivity {

    private static String returnValue;
    private static String [] inputs;
    public static int p_hour;
    public static int p_minutes;
    public static String p_activity;
    public static double p_lat;
    public static double p_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonprocess);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        String qString = extras.getString("qString");
        parseInputs(qString);
        //set something as the return Value? or just return null
    }

    public void parseInputs(String temp) {
        inputs = temp.split(":");
        p_hour = Integer.parseInt(inputs[0]);
        p_minutes = Integer.parseInt(inputs[1]);
        p_activity = inputs[2];
        p_lat = Double.parseDouble(inputs[3]);
        p_long = Double.parseDouble(inputs[4]);
        System.out.println("Hour: " + p_hour + "\nMinutes: " + p_minutes
                        + "\nActivity: " + p_activity + "\nLocation: " + p_lat
                        + "," + p_long);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jsonprocess, menu);
        return true;
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
