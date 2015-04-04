package org.teamscavengr.scavengr;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A task on a hunt.
 */
public class Task {

    private final String id;
    private final Location location;
    private final String clue;
    private final double radius; // in meters
    private final String answer;

    public Task(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
        this.location = new Location(""); // no provider
        this.location.setLatitude(Double.parseDouble(obj.getString("latitude")));
        this.location.setLongitude(Double.parseDouble(obj.getString("longitude")));
        this.clue = obj.getString("clue");
        this.radius = Double.parseDouble(obj.getString("radius"));
        this.answer = obj.getString("answer");
    }

    public String getId() {
        return id;
    }

    public String getClue() {
        return clue;
    }

    public double getRadius() {
        return radius;
    }

    public Location getLocation() {
        return location;
    }

    public String getAnswer() { return answer; }

    public double distanceFrom(LatLng pointFrom) {
        return CalcLib.distanceFromLatLng(new LatLng(this.location.getLatitude(), this.location.getLongitude()), pointFrom);
    }
}
