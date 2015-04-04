package org.teamscavengr.scavengr;

import android.util.Log;

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
 * A hunt is a hunt.
 */
public class Hunt {

    private final String name;
    private final String id;
    private final String[] reviewIds;
    private final Task[] tasks;

    public Hunt(final String name, final String id, String[] reviewIds, Task[] tasks) {
        this.name = name;
        this.id = id;
        this.reviewIds = reviewIds;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String[] getReviewIds() {
        return reviewIds;
    }

    public Task[] getTasks() {
        return tasks;
    }

    /**
     * Loads a hunt object on the current thread. Assumes a network connection is present.
     * @param id The ID of the hunt in our database.
     * @return The Hunt object.
     */
    public static Hunt loadHunt(String id) throws IOException {
        InputStream in = null;
        try {
            URL url = new URL("http://scavengr.meteor.com/hunts/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("SCV", "Got response from scavengr.meteor.com");
            in = conn.getInputStream();

            // Read data
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            // Handle JSON
            JSONObject obj = new JSONObject(sb.toString());
            return new Hunt(id, obj.getString("name"), fromJSONArray(obj.getJSONArray("reviews")),
                    tasksFromJSONArray(obj.getJSONArray("tasks")));

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("id \"" + id + "\" leads to Malformed URL", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
        } finally {
            if(in != null) {
                in.close();
            }
        }
    }

    private static Task[] tasksFromJSONArray(final JSONArray tasks) throws JSONException {
        Task[] ret = new Task[tasks.length()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = new Task(tasks.getJSONObject(i));
        }
        return ret;
    }

    private static String[] fromJSONArray(JSONArray array) throws JSONException {
        String[] ret = new String[array.length()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = array.getString(i);
        }
        return ret;
    }
}
