package com.garygrossi.garfieldwear;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Gary on 5/30/2015.
 */
public class SendToDataLayerThread  extends Thread{
    String path;
    DataMap dataMap;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity_Mobile";

    // Constructor
    SendToDataLayerThread(String p, DataMap map, GoogleApiClient gac){
        path = p;
        dataMap = map;
        mGoogleApiClient = gac;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {

            // Make a DataRequest and send to data layer
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
            putDataMapRequest.getDataMap().putAll(dataMap);
            PutDataRequest request = putDataMapRequest.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v(TAG, "Data sent to: " + node.getDisplayName());
            } else{
                Log.v(TAG, "ERROR: failed to send message");
            }
        }
    }
}
