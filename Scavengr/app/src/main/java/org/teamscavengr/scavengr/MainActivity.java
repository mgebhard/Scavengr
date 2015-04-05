package org.teamscavengr.scavengr;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import org.teamscavengr.scavengr.createhunt.MyHuntsActivity;
import org.teamscavengr.scavengr.goonhunt.HuntsList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private GoogleApiClient googleApiClient;
    private GeofenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildApiClient();

        manager = new GeofenceManager(this, googleApiClient);
    }

    private synchronized void buildApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
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

    public void onClick(View view) {
        /*Hunt.loadHuntInBackground("e4dbb85d17ea96e135b58a4a", new Hunt.HuntLoadedCallback() {
            @Override
            public void huntLoaded(final Hunt hunt) {
                Toast.makeText(MainActivity.this, "loaded hunt " + hunt.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void huntFailedToLoad(final Exception ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "failed to load hunts", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void numHuntsFound(final int i) {}

        }, true);*/

        switch(view.getId()) {
            case R.id.go_on_hunt:
                //Intent hunt = new Intent(this, HuntsList.class);
                Intent hunt = new Intent(this, HuntsList.class);
                // Pass in Geo Location of user
                this.startActivity(hunt);
                break;
            case R.id.create_hunt:
                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
                this.startActivity(createHuntIntent);
                break;
            case R.id.geofenceButton:
                if(!googleApiClient.isConnected()) {
                    Toast.makeText(this, "client not connected", Toast.LENGTH_SHORT).show();
                    break;
                }
                Location l = new Location("");
                l.setLatitude(10d);
                l.setLongitude(10d);
                manager.addGeofence("testGeofenceInNigeria", l, 1000, Geofence.NEVER_EXPIRE, this,
                        new GeofenceManager.GeofenceListener() {
                            @Override
                            public void geofenceTriggered(final GeofenceManager.GeofenceEvent event) {
                                Log.d("SCV", event.geofenceId);
                            }
                        });
            default:
                break;
        }

    }

    @Override
    public void onConnected(final Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Reconnect?
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(final Status status) {
        if(status.isSuccess()) {
            Toast.makeText(this, "W00T", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Awwww... :(", Toast.LENGTH_SHORT).show();
        }
    }
}
