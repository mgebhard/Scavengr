package org.teamscavengr.scavengr;

import android.os.Parcel;
import android.os.Parcelable;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by megangebhard on 4/5/15.
 */
public class Review implements Parcelable {

        private String id;
        private String huntId;
        private float rating;
        private String comments;

        public Review(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
    }

        public Review(final String id, final String huntId, final float rating, final String comments) {
        this.id = id;
        this.huntId = huntId;
        this.rating = rating;
        this.comments = comments;
    }

        public Review(Parcel in ) {
        readFromParcel(in);
    }

    public String getId() {
        return id;
    }

    public float getRating() {
        return rating;
    }

    public String comments() {
        return comments;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public void saveToServer(final URL baseUrl, String huntId) throws IOException, JSONException {
        URL url = new URL("http://scavengr.meteor.com/hunts/" + huntId + "/tasks");
        Map<String, String> requestMap = new HashMap<String, String>();
        requestMap.put("rating", Float.toString(rating));
        requestMap.put("comments", comments);
        NetworkHelper.doRequest(url, "POST", true, requestMap);//.getString("_str");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeFloat(rating);
        dest.writeString(comments);
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
        id = in.readString();
        rating = in.readFloat();
        comments = in.readString();
    }
}