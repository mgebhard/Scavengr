package org.teamscavengr.scavengr;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A task on a hunt.
 */
public class Task {

    private final String id;
    private final Location location;
    private final String text;
    private final double radius; // in meters

    public Task(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
        this.location = new Location(""); // no provider
        this.location.setLatitude(Double.parseDouble(obj.getString("latitude")));
        this.location.setLongitude(Double.parseDouble(obj.getString("longitude")));
        this.text = obj.getString("text");
        this.radius = Double.parseDouble(obj.getString("radius"));
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public double getRadius() {
        return radius;
    }

    public Location getLocation() {
        return location;
    }
}
