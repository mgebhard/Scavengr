package org.teamscavengr.scavengr.createhunt;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

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
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class CreateHuntActivity extends Activity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient;

    Hunt currentHunt;
    ArrayList<Task> allPoints = new ArrayList<Task>();


    // Defaults to Michigan
    protected double currentLatitude = 43.6867;
    protected double currentLongitude = -85.0102;

    public Location mLastLocation;
    public GoogleMap mapObject;

    private int hour, minute;

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
        mapObject = map;
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,
                currentLongitude), 15));

        for (Task task : allPoints){
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
                Intent reviewCreated = new Intent(this, ReviewCreatedHunt.class);
                reviewCreated.putExtra("currentHunt", currentHunt);
                reviewCreated.putExtra("estimatedTime", hour * 60L + minute);
                reviewCreated.putExtra("estimatedTimeUnit", TimeUnit.MINUTES);
                this.startActivity(reviewCreated);
                break;

            case R.id.add_waypoint:
                Intent createTask = new Intent(this, CreateWaypointActivity.class);
                createTask.putExtra("currentHunt", currentHunt);
                this.startActivity(createTask);
                break;

            case R.id.estimated_time:
                EditText t = (EditText)view;
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateHuntActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(final TimePicker view, final int hourOfDay,
                                                  final int minute) {
                                CreateHuntActivity.this.hour = hourOfDay;
                                CreateHuntActivity.this.minute = minute;
                                ((EditText) findViewById(R.id.estimated_time)).setText(hourOfDay +
                                        ":" + String.format("%02d", minute));
                            }
                        }, 0, 0, true);
                timePickerDialog.setTitle("Enter estimated length");
                timePickerDialog.show();
            default:
                break;
        }
    }
}
