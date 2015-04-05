package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
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

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.Optional;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;


public class CreateHuntActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    Hunt currentHunt;
    private boolean editMode = false;

    protected double currentLatitude = 43.6867;
    protected double currentLongitude = -85.0102;

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

        if (getIntent().hasExtra("currentHunt")) {
            Log.d("MEGAN", "Has task Parcelable extra");
            currentHunt = getIntent().getParcelableExtra("currentHunt");
        } else {
            // Default constructor for hunt
            currentHunt = new Hunt();
        }

        if (getIntent().hasExtra("editMode")){
            editMode = getIntent().getBooleanExtra("editMode", true);
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location mLastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
        }

        buildGoogleApiClient();

        if (editMode){
            setTitle("Edit Your Hunt");
        }

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
        mapObject = map;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,
                currentLongitude), 15));

        for (Task task : currentHunt.getTasks()){
            Location taskLocation = task.getLocation();
            Log.d("MEGAN", "Task " + task.getTaskNumber() + " " + task.getClue());
            map.addMarker(new MarkerOptions()
                    .title("#" + task.getTaskNumber() + " " + task.getAnswer())
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
                Intent reviewCreated = new Intent(this, ReviewCreatedHuntActivity.class);
                reviewCreated.putExtra("currentHunt", (Parcelable) currentHunt);
                reviewCreated.putExtra("currentUser", new User("tim", Optional.<String>empty(),
                        Optional.<String>empty(), "tim@tim.com", "RANDOM_STRING_ID_WOOO"));
                this.startActivity(reviewCreated);
                break;

            case R.id.add_waypoint:
                Intent createTask = new Intent(this, CreateWaypointActivity.class);
                createTask.putExtra("currentHunt", (Parcelable) currentHunt);
                this.startActivity(createTask);
                break;


            default:
                break;
        }
    }
}
