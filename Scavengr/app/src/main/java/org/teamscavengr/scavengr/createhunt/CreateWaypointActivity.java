package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;


/**
 * Created by hzhou1235 on 3/15/15.
 */
public class CreateWaypointActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        View.OnTouchListener, SeekBar.OnSeekBarChangeListener{

    protected GoogleApiClient mGoogleApiClient;

    public GoogleMap mapObject;
    public Location currentLocation;

    private final double maxRadius = 5000.0; //in meters
    private int progress;
    private Circle circle;
    private User currentUser;

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
//        Log.d("MEGAN", "onMapReady Setting location: " + currentLatitude + currentLongitude);
        mapObject = map;
        map.setMyLocationEnabled(true);
        LatLng usersLastKnownLocation = new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(usersLastKnownLocation, 15));
        MarkerOptions marker = new MarkerOptions()
                .title("Your Current Location")
                .snippet("Task number.")
                .position(usersLastKnownLocation);
        map.addMarker(marker);

        final double defaultRadius = 10.0;
        circle = map.addCircle(new CircleOptions()
                .center(usersLastKnownLocation)
                .radius(defaultRadius)
                .strokeColor(Color.argb(256, 0, 0, 256))
                .fillColor(Color.argb(100, 0, 0, 256)));

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

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

        Location curLoc = getIntent().getParcelableExtra("curLoc");
        if(curLoc != null) {
            currentLocation = curLoc;
        } else {
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View map = findViewById(R.id.map);
        map.setOnTouchListener(this);

        SeekBar slider = (SeekBar) findViewById(R.id.radius_bar);
        slider.setOnSeekBarChangeListener(this);

        buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review_hunt, menu);
        return super.onCreateOptionsMenu(menu);
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
                InputMethodManager imm;
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
                Hunt currentHunt = getIntent().getParcelableExtra("currentHunt");
                // Grab the task radius
                double taskRadius = maxRadius * (progress/100.0) * (progress/100.0) + 10.0;
                Task taskAdded = new Task(null, currentLocation,
                        ((EditText)findViewById(R.id.clue)).getText().toString(),
                        ((EditText)findViewById(R.id.answer)).getText().toString(),
                        taskRadius, currentHunt.getTasks().size() + 1);
                currentHunt.addTask(taskAdded);
                Intent addTask = new Intent(this, CreateHuntActivity.class);
                addTask.putExtra("currentHunt", (Parcelable)currentHunt);
                addTask.putExtra("user", currentUser);
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
        Log.d("HELEN", "CHANGED PROGRESS");
        circle.setRadius(maxRadius * (progress/100.0) * (progress/100.0) + 10.0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("HELEN", "START PROGRESS");
        progress = seekBar.getProgress();
        circle.setRadius(maxRadius * ((progress/100.0) * (progress/100.0)) + 10.0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("HELEN", "STOP PROGRESS");
        progress = seekBar.getProgress();
        circle.setRadius(maxRadius * (progress/100.0) * (progress/100.0) + 10.0);
    }
}
