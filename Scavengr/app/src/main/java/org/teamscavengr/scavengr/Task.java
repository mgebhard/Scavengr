package org.teamscavengr.scavengr;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A task on a hunt.
 */
public class Task {

    private String id;
    private Location location;
    private String clue;
    private double radius; // in meters

    public Task(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
        this.location = new Location(""); // no provider
        this.location.setLatitude(Double.parseDouble(obj.getString("latitude")));
        this.location.setLongitude(Double.parseDouble(obj.getString("longitude")));
        this.clue = obj.getString("clue");
        this.radius = Double.parseDouble(obj.getString("radius"));
    }

    public Task(final String id, final Location location, final String clue, final double radius) {
        this.id = id;
        this.location = location;
        this.clue = clue;
        this.radius = radius;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void saveToServer(final URL baseUrl) throws IOException, JSONException {
        URL url = new URL("http://scavengr.meteor.com/hunts/" + id);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("latitude", Double.toString(location.getLatitude()));
        requestMap.put("longitude", Double.toString(location.getLongitude()));
        requestMap.put("clue", clue);
        requestMap.put("radius", Double.toString(radius));
        id = NetworkHelper.doRequest(url, "POST", requestMap).getString("_str");
    }
}
