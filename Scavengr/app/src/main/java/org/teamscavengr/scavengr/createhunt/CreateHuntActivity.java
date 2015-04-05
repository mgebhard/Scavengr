package org.teamscavengr.scavengr.createhunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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


public class CreateHuntActivity extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    protected Set<Task> tasksForCurrentHunt = new HashSet<Task>();

    // Defaults to Michigan
    protected double currentLatitude = 43.6867;
    protected double currentLongitude = - 85.0102;

    public Location mLastLocation;
    public GoogleMap mapObject;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_hunt, menu);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_hunt);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().hasExtra("task")) {
            Log.d("MEGAN", "Has task Parcelable extra");
            Task taskAdded = (Task)getIntent().getParcelableExtra("task");
            if (taskAdded != null) {
                tasksForCurrentHunt.add(taskAdded);
            }
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location mLastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        }


        buildGoogleApiClient();
    }

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
        mapObject = map;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,
                                                                    currentLongitude), 15));

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng here = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 6));
    }

    @Override
    public void onConnectionSuspended(final int i) {

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.finish:
                EditText text = (EditText)findViewById(R.id.estimated_time);
                String value = text.getText().toString(); //store this time
                Intent reviewCreated = new Intent(this, ReviewCreatedHunt.class);
                //store all waypoints
                this.startActivity(reviewCreated);
                break;
            case R.id.add_waypoint:
                Intent addWaypoint = new Intent(this, CreateWaypointActivity.class);
                this.startActivity(addWaypoint);
                break;
            default:
                break;
        }

    }

}
