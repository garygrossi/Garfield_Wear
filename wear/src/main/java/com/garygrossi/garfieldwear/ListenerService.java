package com.garygrossi.garfieldwear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gary on 5/30/2015.
 */
public class ListenerService extends WearableListenerService{
    private static final String TAG = "MainActivity_Wear";
    private static final String DATA_PATH = "/wearable_data";
    private static final String FRAME_KEY_1 = "com.garygrossi.key.frame1";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents){
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }
/*        final List events = FreezableUtils
                .freezeIterable(dataEvents);*/

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        Log.d(TAG, "DataEvent received on Android Wear device");
        DataMap dataMap = null;
        for (DataEvent event : dataEvents){
            // Check for correct data type
            if (event.getType() == DataEvent.TYPE_CHANGED){
                // Check for correct data path
                String path = event.getDataItem().getUri().getPath();
                if(path.equals(DATA_PATH)){
                    Log.d(TAG, "Data validated");
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                } else{Log.d(TAG, "Incorrect data path");}
            }else{Log.d(TAG, "Event type does not match");}
        }

        Asset asset = dataMap.getAsset(FRAME_KEY_1);

        // Broadcast locally
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("asset", asset);
        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

    }

/*    @Override
    public void onMessageReceived(MessageEvent messageEvent){

        if(messageEvent.getPath().equals("/message_path")){
            // Put message into local String
            final String message = new String(messageEvent.getData());
            Log.v(TAG, "Message path received on watch is: " + messageEvent.getPath());
            Log.v(TAG, "Message received on watch is: " + message);

            // Broadcast locally
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else{
            super.onMessageReceived(messageEvent);
        }
    }*/

}
