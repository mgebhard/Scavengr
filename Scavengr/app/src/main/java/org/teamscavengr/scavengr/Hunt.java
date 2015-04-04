package org.teamscavengr.scavengr;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String name;
    private String id;
    private String[] reviewIds;
    private Task[] tasks;

    /**
     * If we're creating the first hunt, id should be null.
     */
    public Hunt(final String name, final String id, String[] reviewIds, Task[] tasks) {
        this.name = name;
        this.id = id;
        this.reviewIds = reviewIds;
        this.tasks = tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     * Saves this hunt to the server. If we don't currently have an ID, creates one.
     * @throws IOException If bullshit happens
     */
    public void saveHunt() throws IOException {
        try {
            URL url = null;
            if(id == null) {
                url = new URL("http://scavengr.meteor.com/hunts/");

                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("name", name);
                id = NetworkHelper.doRequest(url, "POST", requestMap).getString("_str");
                requestMap.clear();
                url = new URL("http://scavengr.meteor.com/hunts/" + id);
            }
            // TODO Save the reviews

            // TODO Save the tasks
            url = new URL("http://scavengr.meteor.com/hunts/" + id + "/tasks");
            NetworkHelper.doRequest(url, "DELETE", new HashMap<String, String>());
            for(Task t : tasks) {
                t.saveToServer(url);
            }

        } catch(MalformedURLException ex) {
            throw new RuntimeException("bad url", ex);
        } catch (JSONException ex) {
            throw new RuntimeException("server returned bad data", ex);
        }
    }

    /**
     * Loads a hunt object on the current thread. Assumes a network connection is present.
     * @param id The ID of the hunt in our database.
     * @return The Hunt object.
     */
    public static Hunt loadHunt(String id) throws IOException {
        try {
            URL url = new URL("http://scavengr.meteor.com/hunts/" + id);
            JSONObject obj = NetworkHelper.doRequest(url, "GET", new HashMap<String, String>());
            return new Hunt(id, obj.getString("name"), fromJSONArray(obj.getJSONArray("reviews")),
                    tasksFromJSONArray(obj.getJSONArray("tasks")));

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("id \"" + id + "\" leads to Malformed URL", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
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
                            hlc.numHuntsFound(1);
                            hlc.huntLoaded(h);
                        }
                    });
                } catch (IOException | RuntimeException ex) {
                    Hunt.run(onUIThread, new Runnable() {
                        @Override
                        public void run() {
                            hlc.numHuntsFound(1);
                            hlc.huntFailedToLoad(ex);
                        }
                    });
                }
            }
        }).start();
    }

    public static List<Optional<Hunt>> loadAllHunts() {
        InputStream in = null;
        try {
            URL url = new URL("http://scavengr.meteor.com/hunts/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(1000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            // int response = conn.getResponseCode();
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
            @SuppressWarnings("unchecked")
            List<Optional<Hunt>> ret = new ArrayList<>();
            for(int i = 0; i < obj.length(); i++) {
                try {
                    ret.add(Optional.of(loadHunt(obj.getJSONObject(i).getString("id"))));
                } catch(JSONException | RuntimeException ex) {
                    ret.add(Optional.<Hunt>empty());
                }
            }
            return ret;

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("bad url", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
        } catch (IOException e) {
            // do something
        } finally {
            if(in != null) try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("could not close!", e);
            }
        }

        return Arrays.asList(Optional.<Hunt>empty());
    }

    public static void loadAllHuntsInBackground(final HuntLoadedCallback hlc,
                                                final boolean onUIThread) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Optional<Hunt>> h = loadAllHunts();
                Hunt.run(onUIThread, new Runnable() {
                    @Override
                    public void run() {
                        hlc.numHuntsFound(h.size());
                    }
                });
                for(final Optional<Hunt> hunt : h) {
                    if(hunt.isPresent()) {
                        Hunt.run(onUIThread, new Runnable() {
                            @Override
                            public void run() {
                                hlc.huntLoaded(hunt.get());
                            }
                        });
                    } else {
                        Hunt.run(onUIThread, new Runnable() {
                            @Override
                            public void run() {
                                hlc.huntFailedToLoad(new RuntimeException("not present"));
                            }
                        });
                    }
                }
            }
        }).start();
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

        /**
         * Called when we know how many hunts there will be.
         * @param i The number of hunts there will be.
         */
        public void numHuntsFound(int i);

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
