package mit.location;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        LocationListener {

    LocationManager locationManager;
    public File root = android.os.Environment.getExternalStorageDirectory();
    public File locationLog = new File(root.getAbsolutePath(), "locationLog.txt");
    public FileOutputStream fos;
    public PrintWriter pw;
    public Boolean isLocationServiceOn = false;
    public final static int REQUEST_LOCATION_UPDATE_TIMER =  10*1000;
    public final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 5;
    public Button locationButton;


    private static final String REMOTE_LOCATION =
            "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";

    private static final int REMOTE_SIZE = 650_924;

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
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void toggleLocationService() {
        if (isLocationServiceOn) {
            locationButton.setText("Start Location Services");
            locationManager.removeUpdates(this);
            pw.flush();
            pw.close();
            try {
                fos.close();
            } catch (IOException ignored) {

            }

        } else{
            locationButton.setText("Stop Location Services");
            startLocationUpdates();
        }
        isLocationServiceOn = !isLocationServiceOn;

    }

    public void logDownloadSpeed() {
        new Downloader(this, (ProgressBar) findViewById(R.id.progressBar),
                (TextView) findViewById(R.id.downloadResult)).execute(REMOTE_LOCATION);
    }

    private class Downloader extends AsyncTask<String, Integer, Void> {

        private final Activity context;
        private final ProgressBar bar;
        private final TextView resultView;
        private final NetworkInfo info;

        private long transmittedBefore;
        private long receivedBefore;

        private long downloadTotalTime;

        public Downloader(final Activity context, final ProgressBar bar, final TextView resultView) {
            this.context = context;
            this.bar = bar;
            this.resultView = resultView;
            // Get connection type
            info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if(!(info != null && info.isConnected())) {
                throw new IllegalStateException("could not connect to internet");
            }
        }

        @Override
        protected void onPreExecute() {
            bar.setMax(REMOTE_SIZE);
        }

        @Override
        protected void onPostExecute(final Void _) {
            long dt = TrafficStats.getTotalTxBytes() - transmittedBefore;
            long dr = TrafficStats.getTotalRxBytes() - receivedBefore;

            String data = "Transmitted: " + dt + "\nReceived: " +
                               dr + "\nTime taken: " + downloadTotalTime +
                               "\nDownload speed: " + (int) (1d * dr / downloadTotalTime) +
                               "\nNetwork type: " + info.getTypeName() +
                               "\nNetwork state: " + info.getDetailedState().name() +
                               "\nExtra info: " + info.getExtraInfo();
            resultView.setText(data);
            // write data to log file, with an extra newline
            try {
                File f = new File(root.getAbsolutePath(), "downloadLogs.txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                FileWriter fw = new FileWriter(f, true);
                fw.write(data);
                fw.write("\n\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(final String... params) {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) new URL(params[0]).openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                long preConnectionTime = System.currentTimeMillis();
                connection.connect();
                final long latency = System.currentTimeMillis() - preConnectionTime;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "latency: " + latency + " ms", Toast.LENGTH_LONG)
                                .show();
                    }
                });
                InputStream in = connection.getInputStream();
                transmittedBefore = TrafficStats.getTotalTxBytes();
                receivedBefore = TrafficStats.getTotalRxBytes();
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                int n;
                long downloadStartTime = System.currentTimeMillis();
                while((n = in.read(buffer)) != -1) {
                    // Update progress bar
                    bytesRead += n;
                    publishProgress(bytesRead);
                }
                publishProgress(REMOTE_SIZE);
                downloadTotalTime = System.currentTimeMillis() - downloadStartTime;
            } catch (MalformedURLException e) {
                throw new InternalError("bad code");
            } catch (IOException e) {
                throw new RuntimeException("could not connect", e);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            bar.setProgress(values[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get location service
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationButton = (Button) findViewById(R.id.location);

    }

    public void startLocationUpdates() {
        Criteria cr = new Criteria();
        locationManager.requestLocationUpdates(
                locationManager.getBestProvider(cr, true), // GPS_PROVIDER
                REQUEST_LOCATION_UPDATE_TIMER, // 5*60*1000
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER, // 500
                this,
                getMainLooper());
        try {
            fos = new FileOutputStream(locationLog);
            pw = new PrintWriter(fos);
            pw.print("Starting Location Services: \n");
        } catch (FileNotFoundException ignored) {

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
        String text = getLocationText(location);
        Toast toast = Toast.makeText(this, text,  Toast.LENGTH_LONG);
        toast.show();
        Log.d("DEBUG", text);
        if (isExternalStorageWritable()) {
            pw.print(text);
        }
    }

    public String getLocationText(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String provider = location.getProvider();
        float accuracy = location.getAccuracy();
        float speed = location.getSpeed();
        return String.format("TimeStamp: %d, Lat: %f, Long: %f, Provider: %s, Accuracy: %f, Speed %f \n\n",
                System.currentTimeMillis(), lat, lng, provider, accuracy, speed);
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
