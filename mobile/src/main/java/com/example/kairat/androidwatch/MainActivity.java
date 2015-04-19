package com.example.kairat.androidwatch;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.util.Random;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
