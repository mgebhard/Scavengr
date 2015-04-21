package org.teamscavengr.scavengr;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Geofence manager
 *
 * Created by zrneely on 4/5/15.
 */
public class GeofenceManager  {

    private class GeofenceTransitionsService extends IntentService {

        /**
         * @param name Used to name the worker thread, important only for debugging. If you
         *             don't know what to do, then just pass in "".
         */
        public GeofenceTransitionsService(final String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(final Intent intent) {
            GeofencingEvent event = GeofencingEvent.fromIntent(intent);
            if(event.hasError()) {
                throw new RuntimeException("Geofencing error: " + event.getErrorCode());
            }

            int transition = event.getGeofenceTransition();
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
               transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                List<Geofence> triggered = event.getTriggeringGeofences();
                for(Geofence fence : triggered) {
                    GeofenceManager.this.listenerHashMap.get(fence.getRequestId())
                            .geofenceTriggered(new GeofenceEvent(transition, fence.getRequestId()));
                }
            } else {
                Log.e("SCV", "???");
            }
        }
    }

    public static interface GeofenceListener {

        /**
         * Called when a geofence is triggered. Triggers include entering a geofence,
         * exiting a geofence, and nothing else.
         * @param event An event object.
         */
        public void geofenceTriggered(GeofenceEvent event);

    }

    public static class GeofenceEvent {

        public static final int ENTERED_GEOFENCE = Geofence.GEOFENCE_TRANSITION_ENTER;
        public static final int EXITED_GEOFENCE = Geofence.GEOFENCE_TRANSITION_EXIT;

        public final int type;
        public final String geofenceId;

        public GeofenceEvent(final int type, final String geofenceId) {
            this.type = type;
            this.geofenceId = geofenceId;
        }
    }

    private final Map<String, GeofenceListener> listenerHashMap;

    private PendingIntent pi;

    private final Activity activity;
    private final GoogleApiClient client;

    /**
     * Make a GeofenceManager.
     */
    public GeofenceManager(Activity activity, GoogleApiClient client) {
        listenerHashMap = new HashMap<>();

        this.activity = activity;
        this.client = client;

        Intent intent = new Intent(activity, GeofenceTransitionsService.class);
        pi = PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * ADD A GEOFENCE
     * @param name The name of the geofence.
     * @param center The center of the geofence.
     * @param radius The radius of the geofence.
     * @param timeToLive How long to stay alive. Maybe Geofence.NEVER_EXPIRE
     * @param statusCallback Called when the geofence is added or not or something.
     * @param geofenceListener A listener. Gr8 m8.
     * @return True if it worked.
     */
    public boolean addGeofence(String name, Location center, float radius, long timeToLive,
                              ResultCallback<Status> statusCallback,
                              GeofenceListener geofenceListener) {
        Log.d("MEGAN", "connected || connecting: " + client.isConnected() + client.isConnecting());
        if(!client.isConnected() || pi == null) return false;

        listenerHashMap.put(name, geofenceListener);

        Geofence gf = new Geofence.Builder()
                .setRequestId(name)
                .setCircularRegion(center.getLatitude(), center.getLongitude(), radius)
                .setExpirationDuration(timeToLive)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        GeofencingRequest gfr = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(gf).build();

        LocationServices.GeofencingApi.addGeofences(client, gfr, pi).setResultCallback(statusCallback);

        return true;
    }

    /**
     * Remove every active geofence.
     * @param restart If false, doesn't recreate the service.
     * @return True if it worked.
     */
    public boolean removeGeofences(boolean restart) {
        if(!client.isConnected() || pi == null) return false;
        LocationServices.GeofencingApi.removeGeofences(client, pi);
        pi.cancel();
        if(restart) {
            Intent intent = new Intent(activity, GeofenceTransitionsService.class);
            pi = PendingIntent.getService(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            pi = null;
        }
        return true;
    }

    /**
     * @param names Remove all these geofences (by name).
     * @return True if it worked.
     */
    public boolean removeGeofences(String... names) {
        if(!client.isConnected() || pi == null) return false;
        LocationServices.GeofencingApi.removeGeofences(client, Arrays.asList(names));
        return true;
    }

}
