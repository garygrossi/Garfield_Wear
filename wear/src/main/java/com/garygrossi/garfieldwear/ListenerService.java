package com.garygrossi.garfieldwear;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Gary on 5/30/2015.
 */
public class ListenerService extends WearableListenerService{
    private static final String TAG = "MainActivity_Wear";

    @Override
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
    }
}
