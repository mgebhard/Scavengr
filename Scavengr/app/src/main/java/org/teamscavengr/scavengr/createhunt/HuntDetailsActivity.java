package org.teamscavengr.scavengr.createhunt;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

/**
 * Created by hzhou1235 on 3/30/15.
 */
public class HuntDetailsActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;

    public Location mLastLocation;
    public GoogleMap mapObject;
    private Hunt hunt;
    private User currentUser;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_hunt, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_details);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        //getActionBar().setIcon(R.drawable.scavengr_logo);

        if (MainActivity.hunt != null) {
            hunt = MainActivity.hunt;
        }
        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

        TextView timesHunted = (TextView) findViewById(R.id.times_hunted);
        TextView duration = (TextView) findViewById(R.id.duration);
        timesHunted.setText("Times Hunted: " + "10");

        duration.setText("Estimated Completion Time: " + hunt.getEstimatedTime().first.toString() +
                " " + hunt.getEstimatedTime().second.toString().toLowerCase());

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
        Pair<LatLng, Double> geoFence = CalcLib.calculateCentroidAndRadius(hunt);
        LatLng centroid = geoFence.first;
        Double radius = geoFence.second;

        for (Task task : hunt.getTasks()){
            Location taskLocation = task.getLocation();
            double lat = taskLocation.getLatitude();
            double lng = taskLocation.getLongitude();

            map.addMarker(new MarkerOptions()
                    .title("#" + task.getTaskNumber() + " " + task.getAnswer())
                    .snippet(task.getClue())
                    .position(new LatLng(lat, lng)));
        }

        mapObject = map;
        map.setMyLocationEnabled(true);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, 6));
        //COMMENTED OUT CODE BELOW SHOULD ZOOM OUT TO INCLUDE ALL WAYPOINTS; DEBUG LATER....
        /*if (radius < 0.0002){
            radius = 0.0002;
        }

        Log.d("CENTROID LAT", Double.toString(centroidLat));
        Log.d("CENTROID LNG", Double.toString(centroidLng));
        Log.d("RADIUS", Double.toString(radius));
        LatLngBounds bounds = new LatLngBounds(new LatLng(centroidLat-radius, centroidLng - radius),
                new LatLng(centroidLat+radius, centroidLng + radius));
        map.moveCamera(CameraUpdateFactory.newLatLngBounds (bounds, 10));*/
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
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
            case R.id.edit:
                Intent editHunt = new Intent(this, CreateHuntActivity.class);
                editHunt.putExtra("editMode", true);
                editHunt.putExtra("currentHunt", (Parcelable) hunt);
                editHunt.putExtra("user", currentUser);
                //TODO: edit page
                this.startActivity(editHunt);
                break;

            case R.id.back: //TODO: should this just go back?
                Intent myHunts = new Intent(this, MyHuntsActivity.class);
                myHunts.putExtra("currentHunt", (Parcelable) hunt);
                myHunts.putExtra("user", currentUser);
                this.startActivity(myHunts);
                break;

            default:
                break;
        }

    }
}