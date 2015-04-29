package org.teamscavengr.scavengr;

import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by megangebhard on 4/5/15.
 */
public class Review implements Parcelable {

    private static void run(boolean onUIThread, Runnable r) {
        if(onUIThread) {
            new Handler(Looper.getMainLooper()).post(r);
        } else {
            r.run();
        }
    }

    private String id;
    private String authorId;
    private float rating;
    private String comments;
    private String huntId;

    public Review(JSONObject obj) throws JSONException {
        this.id = obj.getJSONObject("_id").getString("_str");
    }

    public Review(final String id, final String authorId, final float rating, final String comments, final String huntId) {
        this.id = id;
        this.authorId = authorId;
        this.rating = rating;
        this.huntId = huntId;
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

    public String getComments() {
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

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }


    public void saveReview() throws IOException, JSONException {
        try {
            URL url;
            if(id == null) {
                url = new URL("http://scavengr.meteor.com/reviews/");

                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("authorId", authorId);
                requestMap.put("rating", Float.toString(rating));
                requestMap.put("comments", comments);
                requestMap.put("huntId", huntId);
                id = NetworkHelper.doRequest(url, "POST", true, requestMap).getString("_str");
                Log.d("SCV", "Got ID for review");
            } else {
                url = new URL("http://scavengr.meteor.com/reviews/" + id);
                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("authorId", authorId);
                requestMap.put("rating", Float.toString(rating));
                requestMap.put("comments", comments);
                requestMap.put("huntId", huntId);
                NetworkHelper.doRequest(url, "PUT", true, requestMap);
            }
        } catch(IOException ex) {
            throw new RuntimeException("could not save", ex);
        }
    }

    public void saveReviewInBackground(final boolean onUIThread, final ReviewSavedCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    saveReview();
                    Review.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            callback.reviewSaved();
                        }
                    });
                } catch (IOException | JSONException e) {
                    Review.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            callback.reviewFailedToSave(e);
                        }
                    });
                }
            }
        }).start();
    }

    public static Review loadReview(String id) {
        try {
            URL url = new URL("http://scavengr.meteor.com/reviews/" + id);
            JSONObject obj = NetworkHelper.doRequest(url, "GET", false, Collections.<String, String>emptyMap());
            return new Review(id, obj.getString("authorId"), (float) obj.getDouble("rating"), obj.getString("comments"), obj.getString("huntId"));
        } catch (JSONException | IOException e) {
            throw new RuntimeException("could not load review", e);
        }
    }

    public static void loadReviewInBackground(final String id, final ReviewLoadedCallback rlc, final boolean onUIThread) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Review r = loadReview(id);
                    Review.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            rlc.reviewLoaded(r);
                        }
                    });
                } catch (final RuntimeException ex) {
                    Review.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            rlc.reviewFailedToLoad(ex);
                        }
                    });
                }
            }
        }).start();
    }

    public static interface ReviewLoadedCallback {

        public void reviewLoaded(Review r);

        public void reviewFailedToLoad(Exception ex);
    }

    public static interface ReviewSavedCallback {
        public void reviewSaved();
        public void reviewFailedToSave(Exception ex);
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
        dest.writeString(huntId);
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
        huntId = in.readString();
    }
}