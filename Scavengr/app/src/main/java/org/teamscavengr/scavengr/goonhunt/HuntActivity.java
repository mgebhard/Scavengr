package org.teamscavengr.scavengr.goonhunt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseAnalytics;

import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.GeofenceManager;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuntActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LocationListener, GeofenceManager.GeofenceListener,
        ResultCallback<Status> {

    protected final static int REQUEST_LOCATION_UPDATE_TIMER =  10*1000;
    protected final static int REQUEST_LOCATION_UPDATE_MINDISTANCE_METER = 2;
    protected final static String LOCATION_PROVIDER = "network";

    private static final int ACTION_TAKE_PHOTO = 1;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;
    private ArrayList<String> allPhotoPaths = new ArrayList<String>();
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    private long timeStarted;

    protected Location lastKnownLocation;
    protected LatLng centroid;
    protected Location centroidLocation;
    protected Double boundingRadius;
    protected LocationManager locationManager;
    protected boolean inHuntBoundary = false;

    protected Hunt hunt;
    protected User currentUser;
    protected int currentTaskNumber = 0;

    protected Map<Task, Bitmap> images;

    private static GoogleMap mapObject;
    //    private GeofenceManager manager;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hunt);

        // Set album directory based on android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }
        Log.d("HuntActivity", currentUser.toString());

        timeStarted = System.currentTimeMillis();

        if (getIntent().hasExtra("huntObject")) {
            Log.d("HuntActivity", "Hunt found________");
            hunt = (getIntent().getParcelableExtra("huntObject"));
            images = new HashMap<>();

            // Get bounding geo fence for hunt
            Pair<LatLng, Double> geoFence = CalcLib.calculateCentroidAndRadius(hunt);
            centroid = geoFence.first;
            boundingRadius = geoFence.second;
            centroidLocation = new Location("network");
            centroidLocation.setLatitude(centroid.latitude);
            centroidLocation.setLongitude(centroid.longitude);

            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LOCATION_PROVIDER,
                    REQUEST_LOCATION_UPDATE_TIMER,
                    REQUEST_LOCATION_UPDATE_MINDISTANCE_METER,
                    this);
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastKnownLocation != null) {
                lastKnownLocation.setAccuracy(1);
                Log.d("MEGAN", "Found current last location");

                // If not in radius show start screen
                Double distanceFromCentroid = CalcLib.distanceFromLatLng(
                        new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
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
                        "Make sure location is turned on!",
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
//        Log.d("MEGAN", "Location changed to: " + location);
        lastKnownLocation = location;
        Task currentTask;
        if (currentTaskNumber < hunt.getTasks().size()) {
            currentTask = hunt.getTasks().get(currentTaskNumber);
            Double distanceFromAnswer = CalcLib.distanceFromLatLng(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                    new LatLng(currentTask.getLocation().getLatitude(),
                            currentTask.getLocation().getLongitude()));
            Double distanceFromCentroid = CalcLib.distanceFromLatLng(new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude()), centroid);

            if (distanceFromAnswer < currentTask.getRadius()) {
                Log.d("MEGAN", "FOUND TASK");
                loadCompletedTask(currentTaskNumber);
            } else if (!inHuntBoundary && distanceFromCentroid < boundingRadius) {
                // Just entered the hunt boundary
                loadTask(currentTaskNumber);
                inHuntBoundary = true;
            } else if (inHuntBoundary && distanceFromCentroid > boundingRadius) {
                // Exited the hunt boundary
                inHuntBoundary = false;
                // TODO (GEBHARD): PAUSE APP
            }


            List<LatLng> points = new ArrayList<LatLng>();
            points.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            for (Task task : hunt.getTasks()) {
                points.add(new LatLng(task.getLocation().getLatitude(), task.getLocation().getLongitude()));
            }
            LatLng diffLatLng = CalcLib.maxDistanceFromCentroid(centroid, points);
            LatLng northEastCent = new LatLng(centroid.latitude + diffLatLng.latitude * 1.1, centroid.longitude + diffLatLng.longitude * 1.1);
            LatLng southWestCent = new LatLng(centroid.latitude - diffLatLng.latitude * 1.1, centroid.longitude - diffLatLng.longitude * 1.1);
            LatLngBounds bounds = new LatLngBounds(southWestCent, northEastCent);
            mapObject.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20), 500, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    // Do nothing
                }

                @Override
                public void onCancel() {
                    // Do nothing
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapObject = map;
        map.setMyLocationEnabled(true);
        // Based on stack overflow post
        // http://stackoverflow.com/questions/6002563/android-how-do-i-set-the-zoom-level-of-map-view-to-1-km-radius-around-my-curren

//        int zoomLevel = getZoomLevel();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centroid, 14));
//        if (!Double.isNaN(centroid.longitude) || !Double.isNaN(centroid.latitude)) {
//            List<LatLng> points = new ArrayList<LatLng>();
//            points.add(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
//            for (Task task : hunt.getTasks()) {
//                points.add(new LatLng(task.getLocation().getLatitude(), task.getLocation().getLongitude()));
//            }
//            LatLng diffLatLng = CalcLib.maxDistanceFromCentroid(centroid, points);
//            LatLng northEastCent = new LatLng(centroid.latitude + diffLatLng.latitude * 1.1, centroid.longitude + diffLatLng.longitude * 1.1);
//            LatLng southWestCent = new LatLng(centroid.latitude - diffLatLng.latitude * 1.1, centroid.longitude - diffLatLng.longitude * 1.1);
//            LatLngBounds bounds = new LatLngBounds(southWestCent, northEastCent);
//            mapObject.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10), 500, new GoogleMap.CancelableCallback() {
//                @Override
//                public void onFinish() {
//                    // Do nothing
//                }
//
//                @Override
//                public void onCancel() {
//                    // Do nothing
//                }
//            });
//        }

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
    }

    /**
     * Transitions the fragment to show completed task.
     *
     * @param taskNum the task number of the clue you just found
     */
    public void loadCompletedTask(int taskNum){
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
        locationManager.removeUpdates(this);
        if (mGoogleApiClient != null) { mGoogleApiClient.disconnect(); }
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
                // Analytics
                Map<String, String> dims = new HashMap<>();
                dims.put("huntId", hunt.getId());
                if(currentUser != null)
                    dims.put("userId", currentUser.getId());
                dims.put("numWaypoints", Integer.toString(hunt.getTasks().size()));
                dims.put("timeTaken", Long.toString(System.currentTimeMillis() - timeStarted));
                ParseAnalytics.trackEventInBackground("end-hunt", dims);

                // FINISHED SCAVENGER HUNT
                Intent photoRecap = new Intent(this, HuntRecapActivity.class);
                if (hunt != null) {
                    Log.d("Hunt Activity", "The hunt is not null right now");
                    photoRecap.putExtra("hunt", (Parcelable) hunt);
                    photoRecap.putStringArrayListExtra("photoPaths", allPhotoPaths);
                }
                Log.d("HuntActivity", hunt.toString());
                Log.d("HuntActivity", currentUser.toString());
                if (currentUser != null) {
                    photoRecap.putExtra("user", currentUser);
                }
                this.startActivity(photoRecap);
                break;

            case R.id.next_task:
                currentTaskNumber += 1;
                if (currentTaskNumber >= hunt.getTasks().size()){
                    finishedPuzzle();
//                    this.onDestroy();
                } else {
                    loadTask(currentTaskNumber);
                }
                break;

            case R.id.camera:
                // Run a camera intent
                dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                break;

            case R.id.get_hint:
                String hintText = "";
                // Call method with two geo points to get the distance between them then add toast
                if (lastKnownLocation != null) {
                    double distanceFromAnswerInMeters = CalcLib.distanceFromLatLng(
                            lastKnownLocation,
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

            case R.id.found_it:
                // Analytics
                Map<String, String> dimensions = new HashMap<>();
                dimensions.put("huntId", hunt.getId());
                if(currentUser != null)
                    dimensions.put("userId", currentUser.getId());
                dimensions.put("totalNumWaypoints", Integer.toString(hunt.getTasks().size()));
                dimensions.put("waypointNum", Integer.toString(currentTaskNumber));
                ParseAnalytics.trackEventInBackground("found-waypoint", dimensions);


                loadCompletedTask(currentTaskNumber);
                break;

            default:
                break;
        }
    }

    @Override
    public void geofenceTriggered(final GeofenceManager.GeofenceEvent event) {}

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
        Intent home;
        switch (id) {
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
            case R.id.logout:
                LoginManager.getInstance().logOut();
                home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                return super.onOptionsItemSelected(item);
            case R.id.action_home:
                home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                break;
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
    public void onBackPressed() {
        // Don't do anything when he user tries to go back
        DialogInterface.OnClickListener ocl = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        // Exit
                        HuntActivity.super.onBackPressed();
                    case DialogInterface.BUTTON_NEGATIVE:
                        // Don't do anything
                }
            }
        };
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("Are you sure you want to quit this hunt?")
                .setPositiveButton("Yes", ocl)
                .setNegativeButton("No", ocl).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    allPhotoPaths.add(mCurrentPhotoPath);
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
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
        mGoogleApiClient.connect(); // attempt to connect
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {}




}
