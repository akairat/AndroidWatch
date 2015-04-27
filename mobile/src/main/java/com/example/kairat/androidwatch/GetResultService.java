package com.example.kairat.androidwatch;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;

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

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetResultService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "GetResultService";
    private String LOG_MESSAGE = "WebAPIExample";

    List<String> suggested_place_name = new ArrayList<String>();
    List<String> suggested_place_address = new ArrayList<String>();
    List<String> suggested_place_geo = new ArrayList<String>();
    List<String> suggested_place_photo = new ArrayList<String>();
    private String suggested_place_string = null;
    List<String> suggested_place_distance = new ArrayList<String>();
    List<String> suggested_place_duration = new ArrayList<String>();

    int i =0;

    JSONArray place_details = null;
    JSONArray place_list= null;

    private String distance;
    private String duration;


    /*the url for distance API = distancelink+distanceaddress+distancemode*/
    private String distancelink = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=";

    private String destinationsetting = "&destinations=";

    private String distanceaddress = null;
    private String distancemode = "&mode=walking&language=en-EN&sensor=false";
    /*the url for place API = PlaceLink+PlaceLocation+PlaceSetting+PlaceType+PlaceAPIKey*/
    private String PlaceLink = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private String PlaceLocation= "42.3613154,-71.0912821";
    private String PlaceSetting="&radius=500&opennow&types=";
    private String PlaceType="food";

    /*testing to insert photo from photo API, not use*/
    private String PlacePhoto ="https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&maxheight=200&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU";


    private String PlaceAPIKey = "&key=AIzaSyCvsLNrxgNcWPXUav7Aay2lBO6kxqbOvQ8"; //url for the API
    private String apiUrl;

    ResultReceiver receiver;
    Bundle bundle;
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */



    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */



    public GetResultService() {
        super(GetResultService.class.getName());
    }

    int state=0;


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");

        receiver = intent.getParcelableExtra("receiver");

        bundle = new Bundle();

        if (bundle == null) {
            return;
        }

     //   String url = intent.getStringExtra("url");//the var we want from the activity

        //Commented out below because PlaceType and PlaceLocation are passed in as null for some reason?
        //PlaceType = bundle.getString("PlaceType");
        //System.out.println(PlaceType);
        //PlaceLocation = bundle.getString("PlaceLocation");
        //System.out.println(PlaceLocation);


        //   if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            getPlaces(null);
            Log.d(TAG, "Service Started2!");

        } catch (Exception e) {
                /* Sending error message back to activity */
            bundle.putString(Intent.EXTRA_TEXT, e.toString());
            receiver.send(STATUS_ERROR, bundle);
        }

        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    void store_info(Bundle bundle,ResultReceiver receiver){
        String[] place_name = new String[suggested_place_name.size()];
        String[] place_address = new String[suggested_place_address.size()];
        String[] place_geo = new String[suggested_place_geo.size()];
        String[] place_distance = new String[suggested_place_distance.size()];
        String[] place_duration = new String[suggested_place_duration.size()];

                /* Sending result back to activity */
        if (null != place_name && place_name.length > 0) {

            Log.d(TAG, "Service Started3!"+suggested_place_name);
            Log.d(TAG, "Service Started3!"+suggested_place_address);
            Log.d(TAG, "Service Started3!"+suggested_place_geo);
            Log.d(TAG, "Service Started3!"+suggested_place_distance);

            place_name = suggested_place_name.toArray(place_name);
            place_address = suggested_place_address.toArray(place_address);
            place_geo = suggested_place_geo.toArray(place_geo);
            place_distance = suggested_place_distance.toArray(place_distance);
            place_duration = suggested_place_duration.toArray(place_duration);

            bundle.putStringArray("place_name",  place_name);
            bundle.putStringArray("place_address", place_address);
            bundle.putStringArray("place_geo", place_geo);
            bundle.putStringArray("place_distance", place_distance);
            bundle.putStringArray("place_duration", place_duration);

            receiver.send(STATUS_FINISHED, bundle);
        }

    }
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void getPlaces(View view) {
        String encodedInput1 = null;

    /*    try {
            encodedInput1 = URLEncoder.encode(PlaceLocation, "UTF-8");
            encodedInput1 = URLEncoder.encode(PlaceType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_MESSAGE, "Encoding exception");
            e.printStackTrace();
        }

        if (encodedInput1 != null) {*/

            apiUrl = PlaceLink + PlaceLocation + PlaceSetting + PlaceType + PlaceAPIKey; //url for the API
                Log.e(LOG_MESSAGE, "apiUrl:" + apiUrl);
                System.out.println(apiUrl);
            new CallAPI1().execute(apiUrl); //do stuff with URL with parameter
      //  }

    }

    private class CallAPI1 extends AsyncTask<String, String, String> { //this func takes 3 parameter; 1st: retutn type string; onPost exe stuuf; what type onPst takes as string
        //put on a progress bar to async task


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
                getWebResult(null);
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
                suggested_place_geo.add(lat1 + "," + long1);

                //  for(int j=0; j<4; j++)

                if (i == 0)
                    suggested_place_string = lat1 + "," + long1;
                else
                    suggested_place_string = suggested_place_string + "|" + lat1 + "," + long1;
            } catch (JSONException e) {
                Log.e(LOG_MESSAGE, e.getMessage());
                e.printStackTrace();
            }
        }
        //    TextView tv2 = (TextView) findViewById(R.id.textView5);
        //    tv2.setText(suggested_place_string);
        distanceaddress = suggested_place_string;

    }

    /**
     * *****************************Get the distance and time to travel to the places that are close by***************************************
     */
    public void getWebResult(View view) {

        // in an HTTP request string
        String encodedInput = null;

        try {
            encodedInput = URLEncoder.encode(distanceaddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_MESSAGE, "Encoding exception");
            e.printStackTrace();
        }

        if (encodedInput != null) {
            apiUrl = distancelink + PlaceLocation +destinationsetting+distanceaddress + distancemode;
            Log.e(LOG_MESSAGE, "apiUrl:" + apiUrl);
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
                Log.i(LOG_MESSAGE, "result:" + result);
                place_details = jObject.getJSONArray("rows");


            } catch (JSONException e) {
                Log.e(LOG_MESSAGE, "Could not find hits entry in JSON result");
                Log.i(LOG_MESSAGE, e.getMessage());
            }

            if (place_details != null) {
                // showFoodEntries1(foodEntries);
                Log.i(LOG_MESSAGE, "Got it");
                showLocationEntries2(place_details); //nedd to comment these and leave one work
                // updateChoice(null);
            }
        }

    } // end CallAPI


    private JSONArray Location_array;

    private void showLocationEntries2(JSONArray locationEntries) {


        try {
            JSONObject rows = locationEntries.getJSONObject(0);//removed the i
            Log.i(LOG_MESSAGE, "rows:" + rows);
            JSONArray elements_array = rows.getJSONArray("elements");
            Log.i(LOG_MESSAGE, "elements_array:" + elements_array);
            Location_array = elements_array;
            for (i = 0; i < Location_array.length(); i++) {
                try {
                    JSONObject elements = elements_array.getJSONObject(i); //where the reference i is
                    Log.i(LOG_MESSAGE, " elements:" + elements);
                    JSONObject distance1 = elements.getJSONObject("distance");
                    Log.i(LOG_MESSAGE, "distance" + distance1);
                    JSONObject duration1 = elements.getJSONObject("duration");
                    Log.i(LOG_MESSAGE, " duration" + duration1);

                    distance = distance1.getString("text");
                    suggested_place_distance.add(distance);
                    duration = duration1.getString("text");
                    suggested_place_duration.add(duration);
                    int duration_value = duration1.getInt("value");

                }catch (JSONException e) {
                    Log.e(LOG_MESSAGE, e.getMessage());
                    e.printStackTrace();
                }


            }
        }catch (JSONException e) {
            Log.e(LOG_MESSAGE, e.getMessage());
            e.printStackTrace();
        }
        store_info(bundle,receiver);
    }
    //error handling
    public class ResultException extends Exception {

        public ResultException(String message) {
            super(message);
        }

        public ResultException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
