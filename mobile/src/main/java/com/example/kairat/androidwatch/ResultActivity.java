package com.example.kairat.androidwatch;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends ActionBarActivity {


    JSONArray place_details = null;
    JSONArray place_list= null;
    List<String> suggested_place_name = new ArrayList<String>();
    List<String> suggested_place_address = new ArrayList<String>();
    private String suggested_place_string = null;
    List<String> suggested_place_geo = new ArrayList<String>();

    private String longitude;
    private String latitude;
    private String startHour;
    private String startMinute;
    private String pickActivity;

    private String distance;
    private String duration;

    private String LOG_MESSAGE = "WebAPIExample";

    // private String serviceAPI = "https://api.nutritionix.com/v1_1/search/"; //url for the API

    // We truncate to at most 10 results for this demo
    // private String searchresultnumber ="?results=0:";
    //  private String searchParameters = "?results=0:10&fields=item_name,brand_name,brand_id,item_id,nf_calories,item_description,nf_total_carbohydrate,nf_protein,nf_total_fat,nf_cholesterol"; //the result we want to find
    // private String distancelink = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC&destinations=San+Francisco|Victoria+BC&mode=walking&language=en-EN&sensor=false"; //the result we want to find
    private String distancelink = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=-33.8670522,151.1957362&destinations="; //the result we want to find
    private String distanceaddress = null;
    private String distancemode = "&mode=walking&language=en-EN&sensor=false";

    private String PlaceLink = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private String PlaceLocation = "-33.8670522,151.1957362";
    private String PlaceSetting="&radius=500&opennow&types=";
    private String PlaceType="food";
    private String PlacePhoto ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&maxheight=200&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU";

    //-33.8670522,151.1957362&

    private String PlaceAPIKey = "&key=AIzaSyCvsLNrxgNcWPXUav7Aay2lBO6kxqbOvQ8"; //url for the API
    // / this ID and Key is only for the class.  Please don't publish it


    private String apiUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        String qString = extras.getString("qString");
        String[] result_array = qString.split(":");

        startHour =result_array[0].replaceAll(":", "");
        startMinute =result_array[1].replaceAll(":", "");
        pickActivity= result_array[2].replaceAll(":", "");
        PlaceLocation =result_array[3].replaceAll(":", "") +","+result_array[4].replaceAll(":", "");

        //get Your Current Location
      /*  String url2= PlacePhotodd+PlaceAPIKey;

        Drawable imageResource = LoadImageFromWebOperations(url2);
        ImageView imageview= (ImageView)findViewById(R.id.imageView);
       // Drawable res = getResources().getDrawable(imageResource);
        imageview.setImageDrawable(imageResource);
        */
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

    /********************************Get all the places that are close by****************************************/
    public void getPlaces(View view){


        //    String y =  getDistanceInfo("Harvard, Cambridge", "MIT");
        //     TextView tv1 = (TextView) findViewById(R.id.textView3);
        //    tv1.setText(y);

        apiUrl = PlaceLink+PlaceLocation+PlaceSetting+ pickActivity+PlaceAPIKey; //url for the API
        new CallAPI1().execute(apiUrl); //do stuff with URL with parameter

    }

    private class CallAPI1 extends AsyncTask<String, String, String> { //this func takes 3 parameter; 1st: retutn type string; onPost exe stuuf; what type onPst takes as string
        //put on a porgress bar to async task


        @Override
        protected String doInBackground(String... params) { //do in background

            String urlString = params[0]; // URL to call

            HttpURLConnection urlConnection = null;

            InputStream in = null;
            StringBuilder sb = new StringBuilder();

            char[] buf = new char[4096];

            // do the HTTP Get
            try {
                URL url = new URL(urlString); //take the URL and convert it to strin, can't just give string, but need to url data type
                urlConnection = (HttpURLConnection) url.openConnection(); //class: http connection, create connection
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream()); //

                Log.i(LOG_MESSAGE, "got input stream"); //esp when playing wiht web, make some log

                int read;
                while ((read = reader.read(buf)) != -1) {
                    sb.append(buf, 0, read);
                }
            } catch (Exception e) {
                // if any I/O error occurs
                e.printStackTrace();
            } finally {
                if (urlConnection != null) { //free resources
                    urlConnection.disconnect();
                }
                try {
                    // releases any system resources associated with the stream
                    if (in != null) //fee resources
                        in.close();
                } catch (IOException e) {
                    Log.i(LOG_MESSAGE + " Error:", e.getMessage());
                }
            }
            Log.i(LOG_MESSAGE, "Finished reading");
            return sb.toString();
        }


        protected void onPostExecute(String result) {
            //     TextView tv1 = (TextView) findViewById(R.id.textView3);
            //      tv1.setText(result);
            Log.i(LOG_MESSAGE, "starting onPostExecute");

            // JSONArray foodEntries = null;

            // separate this out so people can work on it.
            try {
                JSONObject jObject = new JSONObject(result);
                place_list = jObject.getJSONArray("results");


            } catch (JSONException e) {
                Log.e(LOG_MESSAGE, "Could not find result entry in JSON result");
                Log.i(LOG_MESSAGE, e.getMessage());
            }

            if (place_list != null) {
                // showFoodEntries1(foodEntries);


                Log.i(LOG_MESSAGE, "Got it");
                generate_place(place_list);
                //               showFoodEntries3(foodEntries);
                // showFoodEntries4(place_details);
            }
        }

    } // end CallAPI

    private void generate_place(JSONArray locationEntries) {

        for (i = 0; i < locationEntries.length(); i++) {
            try {
                JSONObject results = locationEntries.getJSONObject(i);
                Log.i(LOG_MESSAGE, "results:" + results);
                JSONObject geo = results.getJSONObject("geometry");
                JSONObject location = geo.getJSONObject("location");
                String lat1 = String.valueOf(location.getDouble("lat"));
                String long1 = String.valueOf(location.getDouble("lng"));

                String name = results.getString("name");
                Log.i(LOG_MESSAGE, "name:" + name);
                String vicinity = results.getString("vicinity");
                Log.i(LOG_MESSAGE, "vicinity:" + vicinity);

                //    TextView tv = (TextView) findViewById(R.id.textView4);
                //    tv.setText(name);
                suggested_place_name.add(name);
                suggested_place_address.add(vicinity);
                suggested_place_geo.add(lat1+","+long1);
                if (i==0)
                    suggested_place_string =  lat1+","+long1;
                else
                    suggested_place_string = suggested_place_string + "|" + lat1+","+long1;
            }catch(JSONException e){
                Log.e(LOG_MESSAGE, e.getMessage());
                e.printStackTrace();
            }
        }
        //    TextView tv2 = (TextView) findViewById(R.id.textView5);
        //    tv2.setText(suggested_place_string);
        distanceaddress = suggested_place_string;
    }
    /********************************Get the distance and time to travel to the places that are close by****************************************/
    public void getWebResult(View view) {
        // "pizza" here is just a demo input for testing.  In the real app, we'd want to get input
        // from the user
        // EditText number = (EditText) findViewById(R.id.editText2);
        //String resultnumer = number.getText().toString();
        //searchParameters =  searchParameters;

        EditText foodeditText = (EditText) findViewById(R.id.editText);

        String food = foodeditText.getText().toString();

        TextView tv = (TextView) findViewById(R.id.textView);
        //  tv.setVisibility(View.INVISIBLE);
        // If we really get input from the user, we'll need to URL encode it before including it
        // in an HTTP request string
        String encodedInput = null;

        try {
            encodedInput = URLEncoder.encode(distanceaddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_MESSAGE, "Encoding exception");
            e.printStackTrace();
        }

        if (encodedInput != null) {
            apiUrl = distancelink + distanceaddress + distancemode ;
            Log.e(LOG_MESSAGE, "apiUrl:" +apiUrl);
            //  apiUrl = searchParameters1;
            new CallAPI().execute(apiUrl); //do stuff with URL with parameter
        }
    }

    private class CallAPI extends AsyncTask<String, String, String> { //this func takes 3 parameter; 1st: retutn type string; onPost exe stuuf; what type onPst takes as string
        //put on a porgress bar to async task


        @Override
        protected String doInBackground(String... params) { //do in background

            String urlString = params[0]; // URL to call

            HttpURLConnection urlConnection = null;

            InputStream in = null;
            StringBuilder sb = new StringBuilder();

            char[] buf = new char[4096];

            // do the HTTP Get
            try {
                URL url = new URL(urlString); //take the URL and convert it to strin, can't just give string, but need to url data type
                urlConnection = (HttpURLConnection) url.openConnection(); //class: http connection, create connection
                InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream()); //

                Log.i(LOG_MESSAGE, "got input stream"); //esp when playing wiht web, make some log

                int read;
                while ((read = reader.read(buf)) != -1) {
                    sb.append(buf, 0, read);
                }
            } catch (Exception e) {
                // if any I/O error occurs
                e.printStackTrace();
            } finally {
                if (urlConnection != null) { //free resources
                    urlConnection.disconnect();
                }
                try {
                    // releases any system resources associated with the stream
                    if (in != null) //fee resources
                        in.close();
                } catch (IOException e) {
                    Log.i(LOG_MESSAGE + " Error:", e.getMessage());
                }
            }
            Log.i(LOG_MESSAGE, "Finished reading");
            return sb.toString();
        }


        protected void onPostExecute(String result) {
            //   TextView tv1 = (TextView) findViewById(R.id.textView3);
            //  tv1.setText(result);
            Log.i(LOG_MESSAGE, "starting onPostExecute");

            // JSONArray foodEntries = null;

            // separate this out so people can work on it.
            try {
                JSONObject jObject = new JSONObject(result);
                Log.i(LOG_MESSAGE, "result:"+ result);
                place_details = jObject.getJSONArray("rows");


            } catch (JSONException e) {
                Log.e(LOG_MESSAGE, "Could not find hits entry in JSON result");
                Log.i(LOG_MESSAGE, e.getMessage());
            }

            if (place_details != null) {
                // showFoodEntries1(foodEntries);


                Log.i(LOG_MESSAGE, "Got it");
                showLocationEntries2(place_details); //nedd to comment these and leave one work
                //               showFoodEntries3(foodEntries);
                // showFoodEntries4(place_details);
            }
        }

    } // end CallAPI


    private JSONArray Location_array;
    private void showLocationEntries2(JSONArray locationEntries) {
        try {
            JSONObject rows = locationEntries.getJSONObject(0);
            Log.i(LOG_MESSAGE, "rows:"+rows);
            JSONArray elements_array = rows.getJSONArray("elements");
            Log.i(LOG_MESSAGE, "elements_array:"+elements_array);
            Location_array= elements_array;
            JSONObject elements = elements_array.getJSONObject(0); //where the reference i is
            Log.i(LOG_MESSAGE, " elements:"+ elements);
            JSONObject distance1 = elements.getJSONObject("distance");
            Log.i(LOG_MESSAGE, "distance"+distance1);
            JSONObject  duration1 = elements.getJSONObject("duration");
            Log.i(LOG_MESSAGE, " duration"+ duration1);

            distance = distance1.getString("text");

            duration = duration1.getString("text");
            int duration_vlaue = duration1.getInt("value");

            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText(distance);
            TextView tv2 = (TextView) findViewById(R.id.textView2);
            tv2.setText(duration);

        } catch (JSONException e) {
            Log.e(LOG_MESSAGE, e.getMessage());
            e.printStackTrace();
        }

    }
    private int i = 0;
    public void updateChoice(View view){
        i++;
        if (i>=Location_array.length())
            i=0;
        try {

            JSONObject elements = Location_array.getJSONObject(i); //where the reference i is
            Log.i(LOG_MESSAGE, " elements:"+ elements);
            JSONObject distance1 = elements.getJSONObject("distance");
            Log.i(LOG_MESSAGE, "distance"+distance1);
            JSONObject  duration1 = elements.getJSONObject("duration");
            Log.i(LOG_MESSAGE, " duration"+ duration1);

            distance = distance1.getString("text");

            duration = duration1.getString("text");
            int duration_value = duration1.getInt("value");

            TextView tv = (TextView) findViewById(R.id.textView);
            tv.setText("Distance: "+ distance);
            TextView tv2 = (TextView) findViewById(R.id.textView2);
            tv2.setText("Time to travel: "+duration);
            TextView tv3 = (TextView) findViewById(R.id.textView4);
            tv3.setText(suggested_place_name.get(i));
            TextView tv4 = (TextView) findViewById(R.id.textView5);
            tv4.setText("Address: " +suggested_place_address.get(i));
        } catch (JSONException e) {
            Log.e(LOG_MESSAGE, e.getMessage());
            e.printStackTrace();
        }
    }
}