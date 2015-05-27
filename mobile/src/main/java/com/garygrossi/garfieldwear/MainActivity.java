package com.garygrossi.garfieldwear;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity  implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String FRAME_KEY_1 = "com.garygrossi.key.frame1";
    private static final String TAG = "MainActivity_Mobile";

    private GoogleApiClient mGoogleApiClient;
    public Bitmap storedBitmap;
    public ImageView image;
    SimpleDateFormat sdf;
    public int currentDateModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Begins background task to download image on startup after generating the url of the day
        currentDateModifier = 0;
        new MyDownloadTask().execute(generateURLString());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
    }

    // A background task that allows downloading of images from a network
    class MyDownloadTask extends AsyncTask<String, Void, Bitmap>
    {
        protected void onPreExecute() {
            // No pre execution tasks are needed
        }

        // Attempts to download an image from the given URL into to a bitmap
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            ImageView image = (ImageView) findViewById(R.id.imageView);
            storedBitmap = result;
            image.setImageBitmap(storedBitmap);

        }
    }

    private void dataMapMaker(){
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Start dataMapMaker...");
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/frame1");
        DataMap map = putDataMapReq.getDataMap();
        map.putLong("time", new Date().getTime());
        map.putAsset(FRAME_KEY_1, createAssetFromBitmap(comicCropLeft(storedBitmap)));
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

        text.setText("Pending result created.");
    }

    public void getContent(View view) {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Attempting to send data....");
        try {
            dataMapMaker();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    // Generates a string of the url to download the image
    public String generateURLString (){
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "http://garfield.com/uploads/strips/" + sdf.format(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * currentDateModifier)) + ".jpg";
    }

    public Bitmap comicCropLeft(Bitmap fullComic){
        return Bitmap.createBitmap(fullComic,27,29,281,243);
    }

    public Bitmap comicCropCenter(Bitmap fullComic){
        return Bitmap.createBitmap(fullComic,319,29,259,243);
    }

    public Bitmap comicCropRight(Bitmap fullComic){
        return Bitmap.createBitmap(fullComic,588,29,281,243);
    }
}
