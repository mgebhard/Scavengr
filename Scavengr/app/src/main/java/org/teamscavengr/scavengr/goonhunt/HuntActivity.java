package org.teamscavengr.scavengr.goonhunt;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;


public class HuntActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    protected GoogleApiClient mGoogleApiClient; // TODO this is already loaded in MainActivity - get that one?

    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected double currentLatitude = 43.6867;
    protected double currentLongitude = -85.0102;

    protected LatLng centroid;
    protected Double boundingRadius;
    protected Circle circle;
    protected double distanceFromCentroid;
    protected LocationManager lm;

    protected Task currentTask;
    protected int currentTaskNumber = 0;

    public Location mLastLocation;
    public GoogleMap mapObject;

    protected Hunt hunt;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_test, menu);
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
        setContentView(R.layout.activity_main_hunt);
        if (getIntent().hasExtra("huntObject")) {

            hunt = (getIntent().getParcelableExtra("huntObject"));
            currentTask = hunt.getTasks().get(currentTaskNumber);

            // Grab and set hunt title

            // TODO (Gebhard): This is not an actual text view
//            TextView titleText = (TextView) findViewById(R.id.textView3);
//            titleText.setText("HUNT: " + hunt.getName());

            // Grab and set hunt description
            //TextView descriptionText = (TextView) findViewById(R.id.textView4);
            //descriptionText.setText(hunt.getDescription());
        }
        /*else {
            Context context = getApplicationContext();
            CharSequence text = "The cat is dead - Failed to load data";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } */

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        mLastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
            Log.d("MEGAN", "Found current last location: " + currentLatitude + currentLongitude);
        }

        try{
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e){
            e.printStackTrace();
            Log.d("HELEN EXCEPTION", e.toString());
        }

        // If not in radius show start screen

        Pair<LatLng, Double> geoFence = CalcLib.calculateCentroidAndRadius(hunt);
        centroid = geoFence.first;
        boundingRadius = geoFence.second;
        Log.d("MEGAN", "BOUNDING RADIUS: " + boundingRadius);


        distanceFromCentroid = CalcLib.distanceFromLatLng(
                 new LatLng(currentLatitude, currentLongitude), centroid);

        if (distanceFromCentroid > boundingRadius) {
            Log.d("MEGAN", "YEAH inside hunt boundaries");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new StartHuntFragment()).commit();
        } else {
            Log.d("MEGAN", "Not inside hunt boundaries");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new TaskFragment()).commit();
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

    public void loadTask() {
        TaskFragment newFragment = new TaskFragment();
        Bundle args = new Bundle();
//        args.putInt(TaskFragment.ARG_POSITION, position);
//        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void completedTask(){
        CompletedTaskFragment newFragment = new CompletedTaskFragment();
        Bundle args = new Bundle();
//        args.putInt(TaskFragment.ARG_POSITION, position);
//        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void finishedPuzzle(){
        CompletedHuntFragment newFragment = new CompletedHuntFragment();
        Bundle args = new Bundle();
//        args.putInt(TaskFragment.ARG_POSITION, position);
//        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }


    @Override
    public void onMapReady(GoogleMap map) {;
        mapObject = map;
        map.setMyLocationEnabled(true);
        // Based on stack overflow post
        // http://stackoverflow.com/questions/6002563/android-how-do-i-set-the-zoom-level-of-map-view-to-1-km-radius-around-my-curren
        int zoomLevel =(int) (16 - Math.log((boundingRadius + distanceFromCentroid) / 500) / Math.log(2));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, zoomLevel));

        circle = map.addCircle(new CircleOptions()
                .center(centroid)
                .radius(boundingRadius * 1.05)
                .strokeColor(Color.argb(256, 0, 0, 256))
                .fillColor(Color.argb(100, 0, 0, 256)));

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(final int i) {
        mGoogleApiClient.connect(); // attempt to reconnect
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.get_photo_recap:
                Intent photoRecap = new Intent(this, HuntRecapActivity.class);
                this.startActivity(photoRecap);
                break;
            case R.id.begin_hunt:
                loadTask();
                break;
            case R.id.next_task:
                loadTask();
//                completedTask();
                break;
            case R.id.camera:
                // Run a camera intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 420);
                finishedPuzzle();
                break;
            case R.id.get_hint:
                double distanceFromAnswerInMeters = CalcLib.distanceFromLatLng(
                        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER),
                        currentTask.getLocation());
                Toast toast = Toast.makeText(this,
                        "You are " + distanceFromAnswerInMeters +
                        " meters away from finding the waypoint",
                        Toast.LENGTH_LONG);
                toast.show();

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        switch(requestCode) {
            case 420: // got a result from the camera button
                // do something with it
                Log.d("SCV", "got a picture, woo: " + data.getData().toString());
        }
    }
}
