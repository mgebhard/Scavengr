package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.Optional;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;


public class CreateHuntActivity extends BaseActivity implements OnMapReadyCallback,
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
                reviewCreated.putExtra("currentUser", new User("RANDOM_STRING_ID_WOOO", "tim", Optional.<String>empty(),
                        Optional.<String>empty()));
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
