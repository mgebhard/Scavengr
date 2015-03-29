package mit.location;

import android.net.TrafficStats;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

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
        long beforeTime = System.currentTimeMillis();
        long transmittedBefore = TrafficStats.getTotalTxBytes();
        long receivedBefore = TrafficStats.getTotalRxBytes();

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
