package org.teamscavengr.scavengr;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A task on a hunt.
 */
public class Task implements Parcelable {

    private String id;
    private Location location;
    private String clue;
    private String answer;
    private double radius; // in meters
    private int taskNumber; //starts 1

    public Task(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
        this.location = new Location(""); // no provider
        this.location.setLatitude(Double.parseDouble(obj.getString("latitude")));
        this.location.setLongitude(Double.parseDouble(obj.getString("longitude")));
        this.clue = obj.getString("clue");
        this.radius = Double.parseDouble(obj.getString("radius"));
        this.answer = obj.getString("answer");
    }

    public Task(final String id, final Location location, final String clue,
                final String answer, final double radius, final int taskNumber) {
        this.id = id;
        this.location = location;
        this.clue = clue;
        this.answer = answer;
        this.radius = radius;
        this.taskNumber = taskNumber;
    }

    public Task(Parcel in ) {
        readFromParcel(in);
    }

    public String getId() {
        return id;
    }

    public String getClue() {
        return clue;
    }

    public Integer getTaskNumber() {
        return taskNumber;
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

    public void setId(String id) {
        this.id = id;
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

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void saveToServer(final URL baseUrl) throws IOException, JSONException {
        URL url = new URL("http://scavengr.meteor.com/hunts/" + id + "/tasks");
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("latitude", Double.toString(location.getLatitude()));
        requestMap.put("longitude", Double.toString(location.getLongitude()));
        requestMap.put("clue", clue);
        requestMap.put("radius", Double.toString(radius));
        requestMap.put("answer", answer);
        id = NetworkHelper.doRequest(url, "POST", true, requestMap).getString("_str");

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        location.writeToParcel(dest, flags);
        dest.writeString(clue);
        dest.writeString(answer);
        dest.writeDouble(radius);
        dest.writeString(id);
        dest.writeInt(taskNumber);
    }

    // NOT SURE IF BELOW IS NEEDED COPYING EXAMPLE
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    private void readFromParcel(Parcel in) {
        location=Location.CREATOR.createFromParcel(in);
        clue = in.readString();
        answer = in.readString();
        radius = in.readDouble();
        id = in.readString();
        taskNumber = in.readInt();
    }
}
