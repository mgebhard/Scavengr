package org.teamscavengr.scavengr.createhunt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.Optional;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.util.ArrayList;
import java.util.List;

public class CreateHuntActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, GoogleMap.OnInfoWindowClickListener, LocationListener {

    protected GoogleApiClient mGoogleApiClient;

    Hunt currentHunt;
    private boolean editMode = false;

    public Location currentLocation;
    public GoogleMap mapObject;
    private User currentUser;

    @Override
    protected void onStop() {
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

        super.onStop();
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

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

        if (getIntent().hasExtra("editMode")){
            editMode = getIntent().getBooleanExtra("editMode", true);
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        currentLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.1f, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0.1f, this);

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
        map.setOnInfoWindowClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),
                currentLocation.getLongitude()), 15));

        int i = 0;
        for (Task task : currentHunt.getTasks()){
            Location taskLocation = task.getLocation();
            LatLng loc = new LatLng(taskLocation.getLatitude(), taskLocation.getLongitude());
            Log.d("MEGAN", "Task " + (i+1) + " " + task.getClue());
            map.addMarker(new MarkerOptions()
                    .title("#" + (i+1) + " " + task.getAnswer())
                    .snippet(task.getClue() + "\n" + "(tap to edit)")
                    .position(loc));
            i++;

            map.addCircle(new CircleOptions()
                    .center(loc)
                    .radius(task.getRadius())
                    .strokeColor(Color.argb(256, 0, 0, 256))
                    .fillColor(Color.argb(100, 0, 0, 256)));
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng here = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 6));
    }

    @Override
    public void onConnectionSuspended(final int i) {}

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {}

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.finish:
                Intent reviewCreated = new Intent(this, ReviewCreatedHuntActivity.class);
                reviewCreated.putExtra("currentHunt", (Parcelable) currentHunt);
                reviewCreated.putExtra("currentUser", new User("RANDOM_STRING_ID_WOOO", "tim", Optional.<String>empty(),
                        Optional.<String>empty()));
                reviewCreated.putExtra("user", currentUser);
                this.startActivity(reviewCreated);
                break;

            case R.id.add_waypoint:
                Intent createTask = new Intent(this, CreateWaypointActivity.class);
                createTask.putExtra("currentHunt", (Parcelable) currentHunt);
                createTask.putExtra("user", currentUser);
                createTask.putExtra("curLoc", currentLocation);
                this.startActivity(createTask);
                break;

            default:
                break;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String num = (marker.getTitle().split("\\s"))[0];
        num = num.replace("#", "");
        int editTaskNum = Integer.parseInt(num)-1;

        Intent editWaypointTask = new Intent(this, CreateWaypointActivity.class);
        editWaypointTask.putExtra("editTaskNum", editTaskNum);
        editWaypointTask.putExtra("currentHunt", (Parcelable) currentHunt);
        editWaypointTask.putExtra("user", currentUser);
        editWaypointTask.putExtra("editMode", editMode);
        this.startActivity(editWaypointTask);
    }

    @Override
    public void onLocationChanged(final Location location) {
        if(isBetterLocation(location, currentLocation)) {
            currentLocation = location;
            if(mapObject != null) {
                Pair<LatLng, Double> geoFence = CalcLib.calculateCentroidAndRadius(currentHunt);
                LatLng centroid = geoFence.first;
                LatLng here =
                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                List<LatLng> points = new ArrayList<LatLng>();
                points.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                for (Task task : currentHunt.getTasks()) {
                    points.add(new LatLng(task.getLocation().getLatitude(), task.getLocation().getLongitude()));
                }
                Log.d("MapLocationChange", centroid.toString());
                if (points.size() > 1 || !Double.isNaN(centroid.longitude) || !Double.isNaN(centroid.latitude) ) {
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
                } else {
                    mapObject.animateCamera(CameraUpdateFactory.newLatLng(here));
                }
//                mapObject.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 14));
            }
        }
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {

    }

    @Override
    public void onProviderEnabled(final String provider) {}

    @Override
    public void onProviderDisabled(final String provider) {}

    private boolean isBetterLocation(Location newLoc, Location curLoc) {
        if(curLoc == null) {
            return true;
        }
        long timeDelta = newLoc.getTime() - curLoc.getTime();
        boolean isNewer = timeDelta > 0;
        if(timeDelta > 1000 * 60 * 2) return true;
        if(timeDelta < -1000 * 60 * 2) return false;

        int accDelt = (int) (newLoc.getAccuracy() - curLoc.getAccuracy());
        boolean isSameProvider = (curLoc.getProvider() == null) ? (newLoc.getProvider() == null) :
                curLoc.getProvider().equals(newLoc.getProvider());
        if(accDelt < 0) return true;
        else if(isNewer && !(accDelt > 0)) return true;
        else if(isNewer && !(accDelt > 200) && isSameProvider) return true;
        return false;
    }

}
