package com.garygrossi.garfieldwear;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

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

    public void getContent(View view) {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Attempting to send data...");
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
