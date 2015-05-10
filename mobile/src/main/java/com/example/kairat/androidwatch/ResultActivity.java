package com.example.kairat.androidwatch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class ResultActivity extends ActionBarActivity implements DownloadResultReceiver.Receiver {
    int i =0;
    private String LOG_MESSAGE = "ResultActivity";

    List<String> suggested_place_name;
    List<String> suggested_place_address;
    List<String> suggested_place_geo;
    List<String> suggested_place_distance;
    List<String> suggested_place_duration;

    private String PlaceType;
    private String PlaceLocation;
    private double lat, lon;
    private String[] selectedPlace =
            {"placename","placeaddress","placedistance","placeduration", "placegeo"}; //Temp option to be shown to the user
                                                                          //only selectedPlace[1] will be passed to the GoogleMaps activity


    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        progress = new ProgressDialog(ResultActivity.this); //start the loading buffer
        this.progress.setMessage("Searching for suggestions nearby!");
        this.progress.show();

       Bundle extras = getIntent().getExtras(); //send bundle to get result  service
        if (extras == null) {
            return;
        }

        String qString = extras.getString("qString");
        String[] result_array = qString.split(":");

        PlaceType= result_array[0];
        lat = Double.parseDouble(result_array[1]);
        lon = Double.parseDouble(result_array[2]);
        PlaceLocation = result_array[1]+","+result_array[2];

        call_intent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");

            return d;
        } catch (Exception e) {
            return null;
        }
    }

    private DownloadResultReceiver mReceiver;
    public void call_intent(){
        /* Starting Download Service */
        mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, GetResultService.class);

/* Send optional extras to Download IntentService */
        intent.putExtra("PlaceType",PlaceType);
        intent.putExtra("PlaceLocation", PlaceLocation);
        Log.i(LOG_MESSAGE, "PlaceType" + PlaceType);
        Log.i(LOG_MESSAGE, "PlaceLocation" + PlaceLocation);
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
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);
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
                updateChoice(null);
                if (progress.isShowing()) { //if the loading buffer is showing, stop it
                    progress.cancel();
                    TextView tv = (TextView) findViewById(R.id.textView4);
                    TextView tv2 = (TextView) findViewById(R.id.textView5);
                    TextView tv3 = (TextView) findViewById(R.id.textView);
                    TextView tv4 = (TextView) findViewById(R.id.textView2);
                    //ImageView iv = (ImageView) findViewById(R.id.imageView3);
                    tv.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.VISIBLE);
                    tv4.setVisibility(View.VISIBLE);

                }
                break;
            case GetResultService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }
    /********************************Get all the places that are close by****************************************/
    int j =0;

    public void updateChoice(View view) {
        TextView tv = (TextView) findViewById(R.id.textView4);
        TextView tv2 = (TextView) findViewById(R.id.textView5);
        TextView tv3 = (TextView) findViewById(R.id.textView);
        TextView tv4 = (TextView) findViewById(R.id.textView2);
        ImageView iv = (ImageView) findViewById(R.id.imageView3);
        ImageButton btn1 = (ImageButton) findViewById(R.id.imageButton);
        ImageButton btn2 = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton btn3 = (ImageButton) findViewById(R.id.imageButton3);

        // List<String> [] PlacesInfo3 = new ArrayList[5];
        if (i==(suggested_place_name.size()-1)) {

            btn2.setImageResource(R.drawable.restart);
        }

        if (i>=suggested_place_name.size()) {
            i = 0;
            btn2.setImageResource(R.drawable.no);
        }



        if (suggested_place_name.get(i).equals("-")){

            String temp = "Sorry, there are no open places nearby!";
            Log.i(LOG_MESSAGE, " PlacesInfo" + temp);
            tv.setText(temp);
            tv2.setText("Try going back and picking a different activity!");
            //tv2.setVisibility(View.INVISIBLE);
            tv3.setVisibility(View.INVISIBLE);
            tv4.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.INVISIBLE);

            btn1.setVisibility(View.INVISIBLE);
            btn2.setVisibility(View.INVISIBLE);
            btn3.setVisibility(View.VISIBLE);
            for(j=0; j<=3; j++)
                selectedPlace[j] = "-";

        }
        else
        {   btn1.setVisibility(View.VISIBLE);
            btn2.setVisibility(View.VISIBLE);
            btn3.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.VISIBLE);
            String temp = suggested_place_name.get(i);
            Log.i(LOG_MESSAGE, " PlacesInfo" + temp);
            tv.setText(temp);
            selectedPlace[0] = temp;

            temp = suggested_place_address.get(i);
            Log.i(LOG_MESSAGE, " PlacesInfo" + temp);
            tv2.setText("Address: " + temp);
            selectedPlace[1] = temp;

            temp = suggested_place_distance.get(i);
            Log.i(LOG_MESSAGE, " PlacesInfo" + temp);
            tv3.setText("Distance: "+temp);
            selectedPlace[2] = temp;


            temp = suggested_place_duration.get(i);
            Log.i(LOG_MESSAGE, " PlacesInfo" + temp);
            tv4.setText("Walking Time: "+ temp);
            selectedPlace[3] = temp;

            selectedPlace[4] = suggested_place_geo.get(i);
            Log.i(LOG_MESSAGE, "Geo " + selectedPlace[4]);
        }
        i++;
    }
    public void closeScreen (View view){
        finish();
    }
    //Starts navigation activity
    public void startNavigation (View view){
        if (selectedPlace[0].equals("-")) {
            Context context = getApplicationContext();
            CharSequence text = "Try picking a different category!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            Intent sn = new Intent(this, Nav.class);
            String info = selectedPlace[4] + ":" + lat + ":" + lon + ":" + selectedPlace[0];
            sn.putExtra("aString", info);
            startActivity(sn);
        }
    }
}