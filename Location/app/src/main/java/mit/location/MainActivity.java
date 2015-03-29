package mit.location;

import android.content.Context;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String REMOTE_LOCATION =
            "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
                // call method to get lat/long and acc
                logLatLongAcc();
                break;
            case R.id.download:
                // call download on PDF
                logDownloadSpeed();
                break;
            default:
                break;
        }
    }

    public void logLatLongAcc() {

    }

    public void logDownloadSpeed() {

        // Begin downloading the file
        long transmittedBefore = TrafficStats.getTotalTxBytes();
        long receivedBefore = TrafficStats.getTotalRxBytes();

        final Context self = this;

        new AsyncTask<String, Double, Void>() {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(self, "latency: " + latency + " ms", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (MalformedURLException e) {
                    throw new InternalError("bad code");
                } catch (IOException e) {
                    throw new RuntimeException("could not connect", e);
                }

                return null;
            }
        }.execute(REMOTE_LOCATION);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
