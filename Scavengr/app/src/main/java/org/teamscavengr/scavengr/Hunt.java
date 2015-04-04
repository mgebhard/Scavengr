package org.teamscavengr.scavengr;

import android.os.Handler;
import android.os.Looper;
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
 * TODO use async tasks for progress monitoring
 */
public class Hunt {

    private static void run(boolean onUIThread, Runnable r) {
        if(onUIThread) {
            new Handler(Looper.getMainLooper()).post(r);
        } else {
            r.run();
        }
    }

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

    /**
     * Loads a hunt in a background thread.
     * @param id The id The id of the user in our database.
     * @param hlc A callback for when the request completes.
     * @param onUIThread If true, the callback will be run on the UI thread.
     */
    public static void loadHuntInBackground(final String id, final HuntLoadedCallback hlc,
                                            final boolean onUIThread) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Hunt h = loadHunt(id);
                    Hunt.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            hlc.huntLoaded(h);
                        }
                    });
                } catch (IOException | RuntimeException ex) {
                    Hunt.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            hlc.huntFailedToLoad(ex);
                        }
                    });
                }
            }
        }).start();
    }

    public static Hunt[] loadAllHunts() throws IOException {
        InputStream in = null;
        try {
            URL url = new URL("http://scavengr.meteor.com/hunts/");
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

            // Chain-load all the other hunts
            JSONArray obj = new JSONArray(sb.toString());
            Hunt[] ret = new Hunt[obj.length()];
            for(int i = 0; i < obj.length(); i++) {
                ret[i] = loadHunt(obj.getString(i));
            }
            return ret;

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("bad url", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
        } finally {
            if(in != null) {
                in.close();
            }
        }
    }

    public static void loadAllHuntsInBackground(final HuntsLoadedCallback hlc,
                                                final boolean onUIThread) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Hunt[] h = loadAllHunts();
                    Hunt.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            hlc.huntsLoaded(h);
                        }
                    });
                } catch (IOException | RuntimeException ex) {
                    Hunt.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            hlc.huntsFailedToLoad(ex);
                        }
                    });
                }
            }
        }).start();
    }

    public static interface HuntsLoadedCallback {

        public void huntsLoaded(Hunt[] hunts);

        public void huntsFailedToLoad(Exception ex);

    }

    public static interface HuntLoadedCallback {

        /**
         * Called when a hunt is successfully loaded.
         * @param hunt The loaded hunt.
         */
        public void huntLoaded(Hunt hunt);

        /**
         * Called when a hunt fails to load. Also called if an exception
         * is thrown by huntLoaded.
         * @param ex The exception thrown.
         */
        public void huntFailedToLoad(Exception ex);

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
