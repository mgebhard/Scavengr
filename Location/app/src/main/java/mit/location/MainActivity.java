package mit.location;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String REMOTE_LOCATION =
            "http://web.mit.edu/21w.789/www/papers/griswold2004.pdf";

    private static final int REMOTE_SIZE = 650_924;

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
        new Downloader(this, (ProgressBar) findViewById(R.id.progressBar),
                (TextView) findViewById(R.id.downloadResult)).execute(REMOTE_LOCATION);
    }

    private static class Downloader extends AsyncTask<String, Integer, Void> {

        private final Activity context;
        private final ProgressBar bar;
        private final TextView resultView;

        private long transmittedBefore;
        private long receivedBefore;

        private long downloadTotalTime;

        public Downloader(final Activity context, final ProgressBar bar, final TextView resultView) {
            this.context = context;
            this.bar = bar;
            this.resultView = resultView;
        }

        @Override
        protected void onPreExecute() {
            bar.setMax(REMOTE_SIZE);
        }

        @Override
        protected void onPostExecute(final Void _) {
            long dt = TrafficStats.getTotalTxBytes() - transmittedBefore;
            long dr = TrafficStats.getTotalRxBytes() - receivedBefore;

            resultView.setText("Transmitted: " + dt + "\nReceived: " +
                               dr + "\nTime taken: " + downloadTotalTime +
                               "\nDownload speed: " + (int) (1d * dr / downloadTotalTime) +
                               " bytes/millisecond");
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
