package org.teamscavengr.scavengr.goonhunt;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.GeofenceManager;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

public class HuntActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LocationListener, GeofenceManager.GeofenceListener,
        ResultCallback<Status> {

    protected final static int REQUEST_LOCATION_UPDATE_TIMER =  10*1000;
    protected final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 5;
    protected final static String LOCATION_PROVIDER = "network";

    protected Location mLastLocation;
    protected LatLng centroid;
    protected Location centroidLocation;
    protected Double boundingRadius;
    protected LocationManager mLocationManager;
    protected boolean inHuntBoundary = false;

    protected Hunt hunt;
    protected int currentTaskNumber = 0;

    private GoogleMap mapObject;
//    private GeofenceManager manager;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hunt);


        if (getIntent().hasExtra("huntObject")) {
            hunt = (getIntent().getParcelableExtra("huntObject"));

//            buildGoogleApiClient();
//            manager = new GeofenceManager(this, mGoogleApiClient);

            // Get bounding geo fence for hunt
            Pair<LatLng, Double> geoFence = CalcLib.calculateCentroidAndRadius(hunt);
            centroid = geoFence.first;
            boundingRadius = geoFence.second;
            centroidLocation = new Location("network");
            centroidLocation.setLatitude(centroid.latitude);
            centroidLocation.setLongitude(centroid.longitude);

            mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(
                    LOCATION_PROVIDER,
                    REQUEST_LOCATION_UPDATE_TIMER,
                    REQUEST_LOCATION_UPDATE_MINDISTANCE_METER,
                    this);
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (mLastLocation != null) {
                mLastLocation.setAccuracy(1);
                Log.d("MEGAN", "Found current last location");

                // If not in radius show start screen
                Double distanceFromCentroid = CalcLib.distanceFromLatLng(
                        new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                        centroid);

                if (distanceFromCentroid > boundingRadius) {
                    Log.d("MEGAN", "Not inside hunt boundaries");
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, new StartHuntFragment()).commit();
                } else {
                    Log.d("MEGAN", "Inside hunt boundaries");
                    inHuntBoundary = true;
                    loadTask(currentTaskNumber);
                }

            } else {
                Log.d("MEGAN", "DON'T KNOW LOCATION");
                Toast toast = Toast.makeText(this,
                        "Make sure location is turned on otherwise can't go hunting",
                        Toast.LENGTH_LONG);
                toast.show();
            }

            try{
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("MEGAN", "Location changed to: " + location);
        mLastLocation = location;
        Task currentTask = hunt.getTasks().get(currentTaskNumber);
        Double distanceFromAnswer = CalcLib.distanceFromLatLng(location, currentTask.getLocation());
        Double distanceFromCentroid = CalcLib.distanceFromLatLng(new LatLng(mLastLocation.getLatitude(),
                mLastLocation.getLongitude()), centroid);

        if (distanceFromAnswer < currentTask.getRadius()) {
            Log.d("MEGAN", "FOUND TASK");
            loadCompletedTask(currentTaskNumber);
        }

        if (!inHuntBoundary && distanceFromCentroid < boundingRadius) {
            loadTask(currentTaskNumber);
            inHuntBoundary = true;
        } else if (inHuntBoundary && distanceFromCentroid > boundingRadius) {
            inHuntBoundary = false;
            // TODO (GEBHARD): PAUSE APP
        }

        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, getZoomLevel()));

    }

    public int getZoomLevel() {
        return (int) (16 - Math.log((boundingRadius + 0) / 500) / Math.log(2));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapObject = map;
        map.setMyLocationEnabled(true);
        // Based on stack overflow post
        // http://stackoverflow.com/questions/6002563/android-how-do-i-set-the-zoom-level-of-map-view-to-1-km-radius-around-my-curren
        int zoomLevel = getZoomLevel();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, zoomLevel));

        map.addCircle(new CircleOptions()
                .center(centroid)
                .radius(boundingRadius)
                .strokeColor(Color.argb(256, 0, 0, 256))
                .fillColor(Color.argb(100, 0, 0, 256)));

    }

    /**
     * Transition the fragment to show a the current task number.
     * Also adds geo fence around current task.
     *
     * @param taskNum number getting loaded indexed at 0
     */
    public void loadTask(int taskNum) {
        Task newTask = hunt.getTasks().get(taskNum);

        TaskFragment newFragmentTask = TaskFragment.newInstance("Clue: " + newTask.getClue(),
                "Task: " + (taskNum+1) + " out of " + Integer.toString(hunt.getTasks().size()));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragmentTask);
        transaction.addToBackStack(null);
        transaction.commit();

        Log.d("MEGAN", "LOADING TASK");

        // Add geofence for this task
//        boolean added = manager.addGeofence("currentTask", newTask.getLocation(), (float) newTask.getRadius(),
//                Geofence.NEVER_EXPIRE, new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(final Status status) {
//                        if(!status.isSuccess()) {
//                            // Could not create geofence :(
//                            Log.e("ZACH", "Could not create geofence!");
//                        }
//                    }
//                }, this);

//        Log.e("MEGAN", "Added geofence for task" + added);
    }

    /**
     * Transitions the fragment to show completed task.
     *
     * @param taskNum the task number of the clue you just found
     */
    public void loadCompletedTask(int taskNum){
        Toast toast = Toast.makeText(this,
                "LOADING COMPLETED TASK",
                Toast.LENGTH_LONG);
        toast.show();

        Task completedTask = hunt.getTasks().get(taskNum);
        CompletedTaskFragment newFragment = CompletedTaskFragment.newInstance(
                "Congratulations! You found: " + completedTask.getAnswer());

        Bundle args = new Bundle();
        args.putParcelable("task", hunt.getTasks().get(currentTaskNumber));
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // SNAIL TRAIL
        Location completedTaskLocation = completedTask.getLocation();
        mapObject.addMarker(new MarkerOptions()
                .position(new LatLng(completedTaskLocation.getLatitude(),
                                    completedTaskLocation.getLongitude()))
                .title(completedTask.getAnswer()));
    }

    /**
     * Transitions the fragment to show a congrats message before exiting this activity.
     */
    public void finishedPuzzle(){
        CompletedHuntFragment newFragment = new CompletedHuntFragment();
        Bundle args = new Bundle();
        args.putParcelable("hunt", hunt);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.get_photo_recap:
                // FINISHED SCAVENGER HUNT
                Intent photoRecap = new Intent(this, HuntRecapActivity.class);
                photoRecap.putExtra("huntObj", (Parcelable) hunt);
                this.startActivity(photoRecap);
                break;

            case R.id.next_task:
                currentTaskNumber += 1;
                if (currentTaskNumber >= hunt.getTasks().size()){
                    finishedPuzzle();
                } else {
                    loadTask(currentTaskNumber);
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
                            hunt.getTasks().get(currentTaskNumber).getLocation());
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

            default:
                break;
        }
    }

    @Override
    public void geofenceTriggered(final GeofenceManager.GeofenceEvent event) {
//        Toast toast = Toast.makeText(this,
//                "GEO CHANGE " + event.geofenceId.toString(),
//                Toast.LENGTH_LONG);
//        toast.show();
//        switch(event.geofenceId) {
//            case "fullHuntFence":
//                if (event.type == Geofence.GEOFENCE_TRANSITION_ENTER) {
//                    if (currentTaskNumber >= hunt.getTasks().size()){
//                        finishedPuzzle();
//                    } else {
//                        loadTask(currentTaskNumber);
//                    }
//                } else {
//                    // TODO(GEBHARD): THEY LEFT HUNT BOUNDARIES -> PAUSE HUNT
//                }
//                break;
//            case "currentTask":
//                if (event.type == Geofence.GEOFENCE_TRANSITION_ENTER) {
//                    // You found the clue, Remove geo fence and switch transitions
////                    manager.removeGeofences("currentTask");
//                    loadCompletedTask(currentTaskNumber);
//                }
//
//            default:
//                // Event for a task - going into or out of a radius
//                Log.d("MEGAN", "Question mark: Default geo event Trigger getting called: " + event.geofenceId);
//        }
    }

    @Override
    public void onResult(final Status status) {
        if(!status.isSuccess()) {
            Log.e("MEGAN", "Could not create geofence");
        }
    }

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
            /*case R.id.change_location:
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
                break;*/
            default:
                break;
        }

        return true;
    }

//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }

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
            case 420:
                // got a result from the camera button
                // do something with it
                Log.d("SCV", "got a picture, woo: " + data.getData().toString());
                //TODO: load the next task instead; also add in detection for completed hunt
                //loadTask(tasksCompleted);
                /*if (tasksCompleted >= hunt.getTasks().size()){ //TODO
                    finishedPuzzle();
                } else {
                    loadTask(tasksCompleted);
                }*/
        }
    }
    @Override
    public void onConnected(Bundle connectionHint) {
//        Log.d("MEGAN", "CONNCETED FUCK YEAA");
//
//        boolean added = manager.addGeofence("fullHuntFence", centroidLocation, boundingRadius.floatValue(),
//                Geofence.NEVER_EXPIRE, this, this);
//
//        Log.d("MEGAN", "GEO FENCE ACTUALLY ADDED: " + added);

    }

    @Override
    public void onConnectionSuspended(final int i) {
        mGoogleApiClient.connect(); // attempt to reconnect
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {}


}
