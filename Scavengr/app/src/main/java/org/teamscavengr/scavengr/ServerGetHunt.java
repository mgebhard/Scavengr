package org.teamscavengr.scavengr;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by erosales on 3/15/15.
 */
public class ServerGetHunt {
    private final JSONArray waypoints;
    private final String huntTitle;
    private final String avgRating;

    public ServerGetHunt(String huntTitle, Optional<JSONArray> waypoints, Optional<String> avgRating) {
        this.huntTitle = huntTitle;
        this.waypoints = waypoints.get();
        this.avgRating= avgRating.get();
    }

    public String getHuntTitle() {
        return huntTitle;
    }

    public JSONArray getWaypoints() {
        return waypoints;
    }

    public ServerGetHunt queryDatabase(LatLng usersLocation) throws IOException {
        InputStream in = null;
        try {
            // Initialize connection
            URL url = new URL("http://scavengr.meteor.com/hunts/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("SPA", "Got response from scavengr.meteor.com");
            in = conn.getInputStream();

            // Read data
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            // Handle JSON
            JSONObject result = new JSONObject(sb.toString());
            Optional<String> avgRating, title;
            Optional<JSONArray> waypoints;
            if(result.has("waypoints")) {
                waypoints = Optional.of(result.getJSONArray("waypoints"));
            } else {
                waypoints = Optional.empty();
            }
            if(result.has("title")) {
                title = Optional.of(result.getString("title"));
            } else {
                title = Optional.empty();
            }
            if(result.has("avgRating")) {
                avgRating = Optional.of(result.getString("avgRating"));
            } else {
                avgRating = Optional.empty();
            }
            return new ServerGetHunt(result.getString("title"), waypoints, avgRating);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("id \"" + usersLocation + "\" lead to Malformed URL", e);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("server returned invalid data");
        } finally {
            if(in != null) {
                in.close();
            }
        }
    }

}
