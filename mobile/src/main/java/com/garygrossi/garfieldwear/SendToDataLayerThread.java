package com.garygrossi.garfieldwear;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Gary on 5/30/2015.
 */
public class SendToDataLayerThread  extends Thread{
    String path;
    String message;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MainActivity_Mobile";

    // Constructor
    SendToDataLayerThread(String p, String msg, GoogleApiClient gac){
        path = p;
        message = msg;
        mGoogleApiClient = gac;
    }

    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();

            if (result.getStatus().isSuccess()) {
                Log.v(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
            } else{
                Log.v(TAG, "ERROR: failed to send message");
            }
        }
    }
}
