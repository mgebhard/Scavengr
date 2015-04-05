package org.teamscavengr.scavengr.mocklocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.io.Closeable;

/**
 * Provides mock locations.
 *
 * To use this, create one, then get a location manager and do
 *
 * locationManager.requestLocationUpdates(whateverNameYouPassedToTheConstructor, minTime, minDist, locationListener).
 *
 * This will give you very convenient location updates whenever the update() method is called on this.
 *
 * If you want this to work with Geofences, you must use the providerName LocationManager.NETWORK_PROVIDER.
 * Also, to get it to work with geofences, you'll probably need to update several times since the geofence
 * algorithm filters out improbable data.
 *
 * Created by zrneely on 4/5/15.
 */
public abstract class MockLocationProvider implements Closeable {

    protected final String providerName;
    private final Context ctx;


    protected MockLocationProvider(final String providerName, final Context ctx) {
        this.providerName = providerName;
        this.ctx = ctx;

        LocationManager lm = getLocationManager();
        lm.addTestProvider(providerName, false, false, false, false, true, true, true, 0, 5);
        lm.setTestProviderEnabled(providerName, true);
    }

    protected LocationManager getLocationManager() {
        return (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void close() {
        getLocationManager().removeTestProvider(providerName);
    }

    /**
     * Updates the provided latitude and longitude.
     */
    public abstract void update();

    protected void update(double lat, double lon) {
        LocationManager lm = getLocationManager();
        Location l = new Location(providerName);
        l.setLatitude(lat);
        l.setLongitude(lon);
        l.setAltitude(0);
        l.setTime(System.currentTimeMillis());
        l.setElapsedRealtimeNanos(System.nanoTime());
        l.setAccuracy(0.5f);
        try {
            lm.setTestProviderLocation(providerName, l);
        } catch(IllegalArgumentException ex) {
            ex.printStackTrace(); // ignore it if the provider is unknown
        }
    }

}
