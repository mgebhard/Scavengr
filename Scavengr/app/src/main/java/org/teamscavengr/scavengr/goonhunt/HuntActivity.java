package org.teamscavengr.scavengr.goonhunt;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.mocklocation.DirectMockLocationProvider;


public class HuntActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LocationListener {

    protected GoogleApiClient mGoogleApiClient; // TODO this is already loaded in MainActivity - get that one?

    protected double currentLatitude = 43.6867;
    protected double currentLongitude = -85.0102;
    private Location mLastLocation;

    public final static int REQUEST_LOCATION_UPDATE_TIMER =  10*1000;
    public final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 5;

    protected LatLng centroid;
    protected Double boundingRadius;
    protected Circle circle;
    protected double distanceFromCentroid;
    protected double distanceFromAnswer;
    protected LocationManager lm;

    protected Task currentTask;
    protected int currentTaskNumber = 0;

    private GoogleMap mapObject;

    protected Hunt hunt;
    private int tasksCompleted = 0;

    /*@Override
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
    } */

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

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.change_location:
                if(BaseActivity.dmlp == null) {
                    Log.e("SCV", "dmlp is null!");
                }

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Location?");
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.stuff, null);
                final EditText lat = (EditText) v.findViewById(R.id.spoof_latitude);
                final EditText lon = (EditText) v.findViewById(R.id.spoof_longitude);

                b.setView(v);
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                });
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        try {
                            Double la = Double.parseDouble(lat.getText().toString());
                            Double lo = Double.parseDouble(lon.getText().toString());
                            Log.d("lat", la.toString());
                            Log.d("lng", lo.toString());
                            BaseActivity.dmlp.setLocation(la, lo);
                            BaseActivity.dmlp.update();

                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }
                    }
                });
                b.show();
                break;
            default:
                break;
        }

        return true;
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
            Log.d("HELEN", "HUNT PASSED");
        } else {
            Log.d("HELEN", "NO HUNT PASSED");
        }
        /*else {
            Context context = getApplicationContext();
            CharSequence text = "The cat is dead - Failed to load data";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } */

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria cr = new Criteria();
        lm.requestLocationUpdates(
                "network",
                REQUEST_LOCATION_UPDATE_TIMER,
                REQUEST_LOCATION_UPDATE_MINDISTANCE_METER,
                this);
        mLastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLastLocation != null) {
            currentLatitude = mLastLocation.getLatitude();
            currentLongitude = mLastLocation.getLongitude();
            mLastLocation.setAccuracy(1);
            Log.d("MEGAN", "Found current last location: " + currentLatitude + currentLongitude);
        }

        try{
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e){
            e.printStackTrace();
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MEGAN", "Location changed to: " + location);
        mLastLocation = location;
        distanceFromAnswer = CalcLib.distanceFromLatLng(location, currentTask.getLocation());

        if (distanceFromAnswer < currentTask.getRadius()) {
            Log.d("MEGAN", "FOUND WAYPOINT");
            loadCompletedTask(currentTaskNumber);
        }
        
        if (mapObject.isMyLocationEnabled())
            Log.d("Ever", "My Location is isEnabled");
        //mapObject.setMyLocationEnabled(true);


        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 13));

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

    public void loadTask(int taskNum) { //taskNum starts at 0
        TaskFragment newFragment = TaskFragment.newInstance("Clue: " + hunt.getTasks().get(taskNum).getClue(), "Task: " + taskNum+1 + " out of " + Integer.toString(hunt.getTasks().size()));
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

        Log.d("HELEN", "LOADING TASK");

        /*TextView taskText = (TextView) newFragment.getView().findViewById(R.id.taskText);
        TextView progressText= (TextView) newFragment.getView().findViewById(R.id.progressText);

        taskText.setText("Clue: " + hunt.getTasks().get(taskNum).getClue());
        progressText.setText("Task: " + taskNum + " out of " + Integer.toString(hunt.getTasks().size()));*/
    }

    public void loadCompletedTask(int taskNum){
        CompletedTaskFragment newFragment = CompletedTaskFragment.newInstance("Congratulations! You found: " + hunt.getTasks().get(taskNum).getAnswer());
        Bundle args = new Bundle();
        args.putParcelable("task", currentTask);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

        //TextView congrats = (TextView) newFragment.getView().findViewById(R.id.congrats);

        //congrats.setText("Congratulations! You found: " + hunt.getTasks().get(taskNum).getAnswer());
    }

    public void finishedPuzzle(){
        CompletedHuntFragment newFragment = new CompletedHuntFragment();
        Bundle args = new Bundle();
        args.putParcelable("hunt", hunt);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.get_photo_recap:
                Intent photoRecap = new Intent(this, HuntRecapActivity.class);
                photoRecap.putExtra("huntObj", (Parcelable) hunt);
                this.startActivity(photoRecap);
                break;

            case R.id.begin_hunt:
                if (tasksCompleted >= hunt.getTasks().size()){
                    finishedPuzzle();
                } else {
                    loadTask(tasksCompleted);
                }
                break;

            case R.id.next_task:
                tasksCompleted += 1;
                currentTaskNumber +=1;
                currentTask = hunt.getTasks().get(tasksCompleted);
                if (tasksCompleted >= hunt.getTasks().size()){
                    finishedPuzzle();
                } else {
                    loadTask(tasksCompleted);
                }
                break;

            case R.id.camera:
                // Run a camera intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 420);
                break;

            case R.id.get_hint:
                String hintText = "";
                // Call method with two geo points to get the distance between them then add toast
                if (mLastLocation != null) {
                    double distanceFromAnswerInMeters = CalcLib.distanceFromLatLng(
                            mLastLocation,
                            currentTask.getLocation());
                    hintText = String.format("You are %.2f meters away from finding the waypoint",
                            distanceFromAnswerInMeters);
                } else {
                    hintText = "Sorry your location dropped.";
                }
                Toast toast = Toast.makeText(this,
                        hintText,
                        Toast.LENGTH_LONG);
                toast.show();
                break;

            case R.id.found_it:
                loadCompletedTask(tasksCompleted); //TODO: shift over to options menu stuff for MVP, geofencing for actual

                break;

            default:
                break;
        }
    }

    /*@Override
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
    }*/

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        switch(requestCode) {
            case 420: // got a result from the camera button
                // do something with it
                Log.d("SCV", "got a picture, woo: " + data.getData().toString());
                //TODO: load the next task instead; also add in detection for completed hunt
                loadTask(tasksCompleted);
                /*if (tasksCompleted >= hunt.getTasks().size()){ //TODO
                    finishedPuzzle();
                } else {
                    loadTask(tasksCompleted);
                }*/
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(final int i) {
        mGoogleApiClient.connect(); // attempt to reconnect
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }
}
