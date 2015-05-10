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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListenerService extends WearableListenerService implements DownloadResultReceiver.Receiver {

    private static final String TAG = "MOBILE MESSAGE: ";
    String PlaceType;

    int i =0;
    private String LOG_MESSAGE = "WebAPIExample";

    List<String> suggested_place_name;
    List<String> suggested_place_address;
    List<String> suggested_place_geo;
    List<String> suggested_place_distance;
    List<String> suggested_place_duration;

    private String PlaceLocation;
    //Stata
    //private String specialPlace = "42.3613154:-71.0912821";

    // Student center
    private String specialPlace = "42.359099, -71.094536";
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
                suggested_place_name = Arrays.asList(place_name);
                suggested_place_address= Arrays.asList(place_address);
                suggested_place_geo = Arrays.asList(place_geo);
                suggested_place_distance = Arrays.asList(place_distance);
                suggested_place_duration = Arrays.asList(place_duration);

                for (int i = 0; i < suggested_place_address.size(); i++){
                    MESSAGE = MESSAGE + suggested_place_name.get(i) + "#"
                            + suggested_place_distance.get(i) + "#"
                            + suggested_place_duration.get(i) + "#"
                            + suggested_place_address.get(i) + "#"
                            + suggested_place_geo.get(i) + "#"
                            + specialPlace + "*";
                }

                Log.i(LOG_MESSAGE, MESSAGE);
                sendChoice();
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
        MESSAGE = "";
        PlaceType = messageEvent.getPath();
        if (PlaceType.charAt(0) == '+'){
            int end = PlaceType.length();
            String coord = PlaceType.substring(2, end);
            Intent i = new Intent(this, Nav.class);
            i.putExtra("aString", coord);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            PlaceLocation = "42.3613154,-71.0912821";
            call_intent();
            initApi();
        }
    }


    //////////////// Methods and fields for sending message/////////////

    private GoogleApiClient client;
    private String nodeId;
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static String MESSAGE;

    /**
     * Initializes the GoogleApiClient and gets the Node ID of the connected device.
     */
    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Method for sending message to the mobile
     */
    public void sendChoice() {
        Log.d(TAG, nodeId);
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Message being sent");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE, null).setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()) {
                                        Log.e(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                    } else {
                                        Log.e(TAG, "message was sent: "+sendMessageResult.getStatus().getStatusCode());
                                    }
                                    //MESSAGE = "";
                                }
                            }
                    );
                    client.disconnect();
                    Log.d(TAG, "Client disconnected");
                }
            }).start();
        }
    }

}
