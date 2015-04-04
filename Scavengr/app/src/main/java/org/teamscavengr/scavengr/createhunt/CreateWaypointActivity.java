package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class CreateWaypointActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    protected Set<Task> tasksForCurrentHunt = new HashSet<Task>();

    // Defaults to Michigan
    protected double currentLatitude = 43.6867;
    protected double currentLongitude = - 85.0102;

    public GoogleMap mapObject;

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("MEGAN", "onMapReady Setting location: " + currentLatitude + currentLongitude);
        LatLng usersLastKnownLocation = new LatLng(currentLatitude, currentLongitude);
        mapObject = map;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLastKnownLocation, 20));

        for (Task task : tasksForCurrentHunt){
            Location taskLocation = task.getLocation();
            map.addMarker(new MarkerOptions()
                    .title(task.getAnswer())
                    .snippet(task.getClue())
                    .position(new LatLng(taskLocation.getLatitude(),
                            taskLocation.getLongitude())));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("MEGAN", "onConnected getting called");
    }

    @Override
    public void onConnectionSuspended(final int i) {
        Log.d("MEGAN", "onConnected getting called");
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        Log.d("MEGAN", "onConnected getting called");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_waypoint);

        // Gets the users last known location to set the flag on the map
        Log.d("MEGAN", "On Create for create way point");
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        Location currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (currentLocation != null) {
            currentLatitude = currentLocation.getLatitude();
            currentLongitude = currentLocation.getLongitude();
            Log.d("MEGAN", "Found current last location: " + currentLatitude + currentLongitude);
        }


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        buildGoogleApiClient();
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
        switch(view.getId()) {
            case R.id.ok:
                //
                this.finish();
                break;
            case R.id.cancel:
                this.finish(); //
                break;
            default:
                break;
        }

    }

}
