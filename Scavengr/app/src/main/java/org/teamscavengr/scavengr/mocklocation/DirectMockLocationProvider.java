package org.teamscavengr.scavengr.mocklocation;

import android.content.Context;

/**
 * Dumbest possible mock location provider.
 *
 * Created by zrneely on 4/5/15.
 */
public class DirectMockLocationProvider extends AbstractMockLocationProvider {

    private double lat, lon;

    protected DirectMockLocationProvider(final String providerName, final Context ctx) {
        super(providerName, ctx);
    }

    public void setLocation(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void update() {
        update(lat, lon);
    }
}
