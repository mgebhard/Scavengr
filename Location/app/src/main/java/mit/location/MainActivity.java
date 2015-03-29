package mit.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        LocationListener {

    LocationManager locationManager;
    public File root = android.os.Environment.getExternalStorageDirectory();
    public File locationLog = new File(root.getAbsolutePath(), "locationLog.txt");
    public FileOutputStream fos;
    public PrintWriter pw;
    public Boolean isLocationServiceOn = false;
    public final static int REQUEST_LOCATION_UPDATE_TIMER = 5*60*1000;
    public final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 500;


    private static final String REMOTE_LOCATION =
            "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                // call method to get lat/long and acc
                toggleLocationService();
                break;
            case R.id.download:
                // call download on PDF
                logDownloadSpeed();
                break;
            default:
                break;
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void toggleLocationService() {
        if (isLocationServiceOn) {
            locationManager.removeUpdates(this);
            pw.flush();
            pw.close();
            try {
                fos.close();
            } catch (IOException e) {

            }

        } else{
            startLocationUpdates();
        }
        isLocationServiceOn = !isLocationServiceOn;

    }

    public void logDownloadSpeed() {

        // Begin downloading the file
        long transmittedBefore = TrafficStats.getTotalTxBytes();
        long receivedBefore = TrafficStats.getTotalRxBytes();

        AsyncTask<String, Double, Long> at = new AsyncTask<String, Double, Long>() {
            @Override
            protected Long doInBackground(final String... params) {
                try {
                    HttpURLConnection connection =
                            (HttpURLConnection) new URL(REMOTE_LOCATION).openConnection();
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    long preConnectionTime = System.currentTimeMillis();
                    connection.connect();
                    return System.currentTimeMillis() - preConnectionTime;
                } catch (MalformedURLException e) {
                    throw new InternalError("bad code");
                } catch (IOException e) {
                    throw new RuntimeException("could not connect", e);
                }
            }
        };
        try {
            long latency = at.execute().get();
            Toast.makeText(this, "latency: " + latency + " ms", Toast.LENGTH_LONG).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get location service
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startLocationUpdates() {
        Criteria cr = new Criteria();
        locationManager.requestLocationUpdates(
                locationManager.getBestProvider(cr, true), // GPS_PROVIDER
                REQUEST_LOCATION_UPDATE_TIMER, // 5*60*1000
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER, // 500
                this);
        try {
            fos = new FileOutputStream(locationLog);
            pw = new PrintWriter(fos);
        } catch (FileNotFoundException e) {

        }
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
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        String provider = location.getProvider();
        float accuracy = location.getAccuracy();
        float speed = location.getSpeed();

        if (isExternalStorageWritable()) {
            pw.printf("TimeStamp: %d, Lat: %d, Long: %d, Provider: %s, Accuracy: %f, Speed %f \n",
                    System.currentTimeMillis(), lat, lng, provider, accuracy, speed);
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
