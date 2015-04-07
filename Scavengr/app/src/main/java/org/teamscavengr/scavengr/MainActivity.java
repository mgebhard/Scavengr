package org.teamscavengr.scavengr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

import org.teamscavengr.scavengr.createhunt.MyHuntsActivity;
import org.teamscavengr.scavengr.goonhunt.HuntsList;
import org.teamscavengr.scavengr.mocklocation.DirectMockLocationProvider;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildApiClient();

        BaseActivity.geofenceManager = new GeofenceManager(this, googleApiClient);

        //HuntActivity.dmlp = BaseActivity.dmlp;

        /*((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .requestLocationUpdates("network", 100L, 0.5f, new LocationListener() {

            @Override
            public void onLocationChanged(final Location location) {
                Toast.makeText(MainActivity.this, "Location changed: " + location.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(final String provider, final int status,
                                        final Bundle extras) {}

            @Override
            public void onProviderEnabled(final String provider) {}

            @Override
            public void onProviderDisabled(final String provider) {}
        });*/

        ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates("network", 10000L, 0.5f, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d("ZACH", location.toString());
            }

            @Override
            public void onStatusChanged(final String provider, final int status,
                                        final Bundle extras) {
            }

            @Override
            public void onProviderEnabled(final String provider) {
            }

            @Override
            public void onProviderDisabled(final String provider) {
            }
        });

        // production build
        if((getApplication().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
            if(checkCallingOrSelfPermission("android.permission.ACCESS_MOCK_LOCATION") ==
               PackageManager.PERMISSION_GRANTED) {
                throw new RuntimeException("In production mode with mock location enabled, idiots!");
            }
        }
    }

    private synchronized void buildApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dmlp != null)
            dmlp.close();
        BaseActivity.geofenceManager.removeGeofences();
    }

    public void onClick(View view) {
        /*Hunt.loadHuntInBackground("e4dbb85d17ea96e135b58a4a", new Hunt.HuntLoadedCallback() {
            @Override
            public void huntLoaded(final Hunt hunt) {
                Toast.makeText(MainActivity.this, "loaded hunt " + hunt.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void huntFailedToLoad(final Exception ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "failed to load hunts", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void numHuntsFound(final int i) {}

        }, true);*/

        switch(view.getId()) {
            case R.id.go_on_hunt:
                //Intent hunt = new Intent(this, HuntsList.class);
                Intent hunt = new Intent(this, HuntsList.class);
                // Pass in Geo Location of user
                this.startActivity(hunt);
                break;
            case R.id.create_hunt:
                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
                this.startActivity(createHuntIntent);
                break;
            case R.id.geofenceButton:
                if(!googleApiClient.isConnected()) {
                    Toast.makeText(this, "client not connected", Toast.LENGTH_SHORT).show();
                    break;
                }
                Location l = new Location("");
                l.setLatitude(42.358801d); // The coords of the stud
                l.setLongitude(-71.094635d);
                BaseActivity.geofenceManager.addGeofence("geofenceStud", l, 100, Geofence.NEVER_EXPIRE, this,
                        new GeofenceManager.GeofenceListener() {
                            @Override
                            public void geofenceTriggered(final GeofenceManager.GeofenceEvent event) {
                                Toast.makeText(MainActivity.this,
                                        event.geofenceId + ": " + ((event.type == GeofenceManager.GeofenceEvent.ENTERED_GEOFENCE) ? "entered" : "exited"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            default:
                break;
        }

    }

    @Override
    public void onConnected(final Bundle bundle) {
        Toast.makeText(this, "gapi client connected", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.setMockMode(googleApiClient, true);
        BaseActivity.dmlp = new DirectMockLocationProvider("network", this);
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // Reconnect?
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        Toast.makeText(this, "gapi client connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(final Status status) {
        if(status.isSuccess()) {
            Toast.makeText(this, "W00T", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Awwww... :(", Toast.LENGTH_SHORT).show();
        }
    }
}
