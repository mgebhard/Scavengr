package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;

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


/**
 * Created by hzhou1235 on 3/15/15.
 */
public class CreateWaypointActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        View.OnTouchListener, SeekBar.OnSeekBarChangeListener{

    protected GoogleApiClient mGoogleApiClient;

    // Defaults to Michigan
    protected double currentLatitude = 43.6867;
    protected double currentLongitude = - 85.0102;

    public GoogleMap mapObject;
    public Location mLastLocation;

    private InputMethodManager imm = null;

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
        LatLng usersLastKnownLocation = new LatLng(currentLatitude, currentLongitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLastKnownLocation, 15));
        map.addMarker(new MarkerOptions()
                .title("Your Current Location")
                .snippet("Task number.")
                .position(usersLastKnownLocation));
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
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        mLastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
            Log.d("MEGAN", "Found current last location: " + currentLatitude + currentLongitude);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View map = findViewById(R.id.map);
        map.setOnTouchListener(this);

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

    /*public void onFocusChange(View view, boolean bool){
        Log.d("HELEN", "ASDF");
        switch(view.getId()){
            case R.id.clue:
                EditText clueText = (EditText) findViewById(R.id.clue);
                Log.d("HELEN", "AFSL;SFAL;KSFDLJFSADJLKFASDL;FDSA");
                imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(clueText.getWindowToken(), 0);
            case R.id.answer:
                EditText editText = (EditText) findViewById(R.id.clue);
                Log.d("HELEN","AFSL;SFAL;KSFDLJFSADJLKFASDL;FDSA");
                imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }*/

    public boolean onTouch(View view, MotionEvent me){ //implement eventually
        switch(view.getId()) {
            case R.id.map:
                Log.d("HELEN", "SHOULD PRINT THIS");
                EditText cText = (EditText) findViewById(R.id.clue);
                EditText aText = (EditText) findViewById(R.id.answer);
                if (cText.requestFocus()){
                    imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cText.getWindowToken(), 0);
                }
                if (aText.requestFocus()){
                    imm = (InputMethodManager) view.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(aText.getWindowToken(), 0);
                }
        }
        return true;
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.ok:
                Intent addTask = new Intent(this, CreateHuntActivity.class);
                EditText clueText = (EditText)findViewById(R.id.clue);
                EditText answerText = (EditText)findViewById(R.id.answer);
                Double defaultRadius = 2.0;
                // Need to get the radius after Helen adds bar
                Task taskAdded = new Task(null, mLastLocation, clueText.getText().toString(),
                                        answerText.getText().toString(), defaultRadius,
                                        getIntent().getIntExtra("taskNumber", 1));
                addTask.putExtra("task", taskAdded);

                this.startActivity(addTask);
                break;
            case R.id.cancel:
                this.finish();
                break;

            default:
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //progress max default is 100

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
    }
}
