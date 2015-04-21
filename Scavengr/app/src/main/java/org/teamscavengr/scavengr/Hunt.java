package org.teamscavengr.scavengr;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A hunt is a hunt.
 *
 * Features to add at some point: number of times hunted
 */
public class Hunt implements Parcelable, Serializable {

    private static void run(boolean onUIThread, Runnable r) {
        if(onUIThread) {
            new Handler(Looper.getMainLooper()).post(r);
        } else {
            r.run();
        }
    }

    private String name;
    private String id;
    private List<String> reviewIds;
    private List<Task> tasks;
    private String description;
    private String creatorId;
    private long estTime;
    private TimeUnit estTimeUnit;
    private long timeCreated; // seconds past epoch

    /**
     * If we're creating the first hunt, id should be null.
     */
    public Hunt(final String id, final String name, List<String> reviewIds, List<Task> tasks, final String description,
        final String creatorId, final long estTime, final TimeUnit estTimeUnit, final long timeCreated) {
        this.name = name;
        this.id = id;
        this.reviewIds = reviewIds;
        this.tasks = tasks;
        this.description = description;
        this.creatorId = creatorId;
        this.estTime = estTime;
        this.estTimeUnit = estTimeUnit;
        this.timeCreated = timeCreated;
    }


    public Hunt(final String name, List<Task> tasks, final long estTime,
                final TimeUnit estTimeUnit, final long timeCreated) {
        this.name = name;
        this.id = null;
        this.reviewIds = new ArrayList<String>();
        this.tasks = tasks;
        this.description = "";
        this.creatorId = "";
        this.estTime = 0;
        this.estTimeUnit = TimeUnit.MINUTES;
        this.timeCreated = 0;
    }

    //DEFAULT
    public Hunt() {
        this.name = "";
        this.id = null;
        this.reviewIds = new ArrayList<String>();
        this.tasks = new ArrayList<Task>();
        this.description = "";
        if (Profile.getCurrentProfile() != null) {
            this.creatorId = Profile.getCurrentProfile().getId();
        } else {
            this.creatorId = "";
        }
        this.estTime = estTime;
        this.estTimeUnit = estTimeUnit;
        this.timeCreated = timeCreated;
    }


    public Hunt(Parcel in) {
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        id = in.readString();
        final int nReview = in.readInt();
        reviewIds = new ArrayList<String>();
        for (int i = 0; i < nReview; i++) {
            reviewIds.add(in.readString());
        }
        final int nTasks = in.readInt();
        tasks = new ArrayList<Task>();
        for (int i = 0; i < nTasks; i++) {
            String taskId = in.readString();
            Location taskLoc = Location.CREATOR.createFromParcel(in);
            String taskClue = in.readString();
            String taskAnswer = in.readString();
            double taskRadius = in.readDouble();
            int taskNumber = in.readInt();
            tasks.add(new Task(taskId, taskLoc, taskClue, taskAnswer, taskRadius, taskNumber));
        }
        description = in.readString();
        creatorId = in.readString();
        estTime = in.readLong();
        estTimeUnit = (TimeUnit) in.readSerializable();
        timeCreated = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(name);
        dest.writeString(id);
        // Write out reviewIds
        dest.writeInt(reviewIds.size());
        for (int i = 0; i < reviewIds.size(); i++) {
            dest.writeString(reviewIds.get(i));
        }
        // Write out tasks
        dest.writeInt(tasks.size());
        for (Task task : tasks) {
            dest.writeString(task.getId());
            task.getLocation().writeToParcel(dest, flags);
            dest.writeString(task.getClue());
            dest.writeString(task.getAnswer());
            dest.writeDouble(task.getRadius());
            dest.writeInt(task.getTaskNumber());
        }
        dest.writeString(description);
        dest.writeString(creatorId);
        dest.writeLong(estTime);
        dest.writeSerializable(estTimeUnit);
        dest.writeLong(timeCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        // TODO(ZACH || EVER): Should iterate through get rating ids from db and average ratings
        // set 4.3 as default rating for MVP
        return 4.3f;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getNumberOfTasks() {return tasks.size();}

    public void setDescription(String desc) { description = desc; }

    public List<String> getReviewIds() {
        return reviewIds;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public Pair<Long, TimeUnit> getEstimatedTime() {
        return new Pair<>(estTime, estTimeUnit);
    }

    public void setEstimatedTime(long time, TimeUnit unit) {
        this.estTime = time;
        this.estTimeUnit = unit;
    }

    public void setEstimatedTime(Pair<Long, TimeUnit> time) {
        this.estTime = time.first;
        this.estTimeUnit = time.second;
    }

    /**
     * Saves this hunt to the server. If we don't currently have an ID, creates one.
     * @throws IOException If bullshit happens
     */
    public void saveHunt() throws IOException {
        try {
            URL url;
            if(id == null) {
                url = new URL("http://scavengr.meteor.com/hunts/");

                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("name", name);
                requestMap.put("creatorId", creatorId);
                requestMap.put("description", description);
                requestMap.put("timeCreated", Long.toString(timeCreated));
                requestMap.put("estimatedTime", Long.toString(estTime));
                requestMap.put("estimatedTimeUnit", estTimeUnit.name());
                id = NetworkHelper.doRequest(url, "POST", true, requestMap).getString("_str");
                requestMap.clear();
                Log.d("ID_ID", id);
            }

            // Make an empty review array.
            url = new URL("http://scavenger.meteor.com/hunts/" + id + "/reviews");
            NetworkHelper.doRequest(url, "POST", true, new HashMap<String, String>());

            // Save the tasks
            url = new URL("http://scavengr.meteor.com/hunts/" + id + "/tasks");
            Log.d("URL",url.toString());
            NetworkHelper.doRequest(url, "DELETE", false, new HashMap<String, String>());
            for(Task t : tasks) {
                t.saveToServer(url, id);

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
            JSONObject obj = NetworkHelper.doRequest(url, "GET", false, new HashMap<String, String>());
            return new Hunt(id, obj.getString("name"), reviewsFromJSONArray(obj),
                    tasksFromJSONArray(obj.getJSONArray("tasks")),
                    obj.getString("description"),
                    obj.getString("creatorId"),
                    Long.parseLong(obj.getString("estimatedTime")),
                    TimeUnit.valueOf(obj.getString("estimatedTimeUnit")),
                    Long.parseLong(obj.getString("timeCreated")));

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
                    ex.printStackTrace();
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

    public static List<Optional<Hunt>> loadAllUserHunts(String userId) {
        InputStream in = null;
        try {
            URL url = new URL("http://scavengr.meteor.com/users/" + userId);
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
            JSONObject obj = new JSONObject(sb.toString());
//            obj.getJSONArray("createdHunts");


            @SuppressWarnings("unchecked")
            List<Optional<Hunt>> ret = new ArrayList<>();



            /*for(int i = 0; i < obj.length(); i++) {
                try {
                    ret.add(Optional.of(loadHunt(obj.getJSONObject(i).getString("id"))));
                } catch(JSONException | RuntimeException ex) {
                    ex.printStackTrace();
                    ret.add(Optional.<Hunt>empty());
                }
            }
            return ret;*/

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

    public static void loadUsersHuntsInBackground(final HuntLoadedCallback hlc,
                                                  final boolean onUIThread, final String userId) {
        new Thread(new Runnable () {
            @Override
            public void run() {
                final List<Optional <Hunt>> h = loadAllUserHunts(userId);
                Hunt.run(onUIThread, new Runnable() {
                    @Override
                    public void run() {
                        hlc.numHuntsFound(h.size());
                    }
                });
                for (final Optional<Hunt> hunt : h) {
                    if (hunt.isPresent()) {
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

    public void makeHuntToast(String toastMessage) {

    }

    public boolean checkHunt(StringBuilder toastString) {

/*
        this.name = "";
        this.id = null;
        this.reviewIds = new ArrayList<String>();
        this.tasks = new ArrayList<Task>();
        this.description = "";
        if (Profile.getCurrentProfile() != null) {
            this.creatorId = Profile.getCurrentProfile().getId();
        } else {
            this.creatorId = "";
        }

        private TimeUnit estTimeUnit;
        private long timeCreated;
*/
        if (name == "" || description == null) {
            toastString.append("Name missing\n");
            return false;
        }
        if (description == "" || description == null) {
            toastString.append("Description missing\n");
            return false;
        }
        if (creatorId == "" || creatorId == null) {
            toastString.append("You must be logged in\n");
            return false;
        }
        if (estTime == 0) {
            toastString.append("Missing Estimated Time\n");
            return false;
        }
        if (estTimeUnit == null) {
            toastString.append("Missing Estimated Time Units\n");
            return false;
        }

        return true;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(final String creatorId) {
        this.creatorId = creatorId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(final long timeCreated) {
        this.timeCreated = timeCreated;
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

    private static List<Task> tasksFromJSONArray(final JSONArray tasks) throws JSONException {
        List<Task> ret = new ArrayList<Task>();
        for(int i = 0; i < tasks.length(); i++) {
            ret.add(new Task(tasks.getJSONObject(i)));
        }
        return ret;
    }

    private static List<String> reviewsFromJSONArray(JSONObject obj) throws JSONException {
        JSONArray array = new JSONArray();
        List<String> ret = new ArrayList<String>(); //[array.length()];
        try {
            array = obj.getJSONArray("reviews");
            for (int i = 0; i < array.length(); i++) {
                ret.add(array.getString(i));
            }
            // Dont worry about it bro.
        } catch (JSONException e ) {
        }
        return ret;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Hunt createFromParcel(Parcel in) {
            return new Hunt(in);
        }

        public Hunt[] newArray(int size) {
            return new Hunt[size];
        }
    };

}
