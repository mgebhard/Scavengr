package org.teamscavengr.scavengr;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Waypoint object.
 *
 * Created by erosales on 3/15/15.
 */
public class Waypoint {
    private final String waypointTitle;
    private final LatLng location;
    private final double mRadius;

    public Waypoint (String waypointTitle, LatLng location, double mRadius){
        this.waypointTitle = waypointTitle;
        this.location = location;
        this.mRadius = mRadius;
    }

    public String getTitle() {
        return waypointTitle;
    }

    public LatLng getLocation() {
        return location;
    }

    public double getCheckRadius() {
        return mRadius;
    }

    public double distanceFrom(LatLng pointFrom) {
        return CalcLib.distanceFromLatLng(this.location, pointFrom);
    }
}
