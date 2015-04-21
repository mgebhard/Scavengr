package org.teamscavengr.scavengr;

import android.content.Context;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A User is a user.
 */
public class User implements Serializable {

    private final String name;
    private final Optional<String> gPlusId;
    private final Optional<String> fbId;
    private final String email;
    private String id;

    public User(String id, String name, Optional<String> gPlusId, Optional<String> fbId, String email) {
        this.id = id;
        this.name = name;
        this.gPlusId = gPlusId;
        this.fbId = fbId;
        this.email = email;
    }

    /**
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return True if this user has connected his/her Google+ account.
     */
    public boolean hasGooglePlus() {
        return gPlusId.isPresent();
    }

    /**
     * @return True if this user has connected his/her Facebook account.
     */
    public boolean hasFacebook() {
        return fbId.isPresent();
    }

    /**
     * @return This users' Facebook ID. This will throw an exception if the user has not
     * connected their facebook account.
     * @see #hasFacebook()
     */
    public String getFacebookId() {
        return fbId.get();
    }

    /**
     * @return This user's Google+ ID. This will throw an exception if the user has not
     * connected their Google+ account.
     * @see #hasGooglePlus()
     */
    public String getGooglePlusId() {
        return gPlusId.get();
    }

    /**
     * @return This user's id in our database.
     */
    public String getId() {
        return id;
    }

    /**
     * Loads a user object on the current thread. Assumes a network connection is
     * present.
     * @param id The id of the user in our database.
     * @return The user object.
     * @throws java.io.IOException If there is an error.
     */
    public static User loadUser(String id) throws IOException {
        try {
            URL url = new URL("http://scavengr.meteor.com/users/" + id);
            JSONObject result = NetworkHelper.doRequest(url, "GET");
            Optional<String> gPlus, fb;
            if(result.has("googleplus")) {
                gPlus = Optional.of(result.getString("googleplus"));
            } else {
                gPlus = Optional.empty();
            }
            if(result.has("facebook")) {
                fb = Optional.of(result.getString("facebook"));
            } else {
                fb = Optional.empty();
            }

            return new User(id, result.getString("name"), gPlus, fb, result.getString("email"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("id \"" + id + "\" lead to Malformed URL", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
        }
    }

    /**
     * Loads a user in a background thread.
     * @param id The id The id of the user in our database.
     * @param ulc A callback for when the request completes.
     * @param onUIThread If true, the callback will be run on the UI thread.
     */
    public static void loadUserInBackground(final String id, final UserLoadedCallback ulc,
                                            final boolean onUIThread) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final User u = loadUser(id);
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                ulc.userLoaded(u);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        ulc.userLoaded(u);
                    }
                } catch (final IOException | RuntimeException e) {
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                ulc.userFailedToLoad(e);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        ulc.userFailedToLoad(e);
                    }
                }
            }
        }).start();
    }

    public static List<String> findUserWithName(final String name) throws IOException {
        System.out.println(name);
        try {
            URL url = new URL("http://scavengr.meteor.com/users/byName/" + URLEncoder.encode(name, "UTF-8"));
            JSONObject result = NetworkHelper.doRequest(url, "GET");
            JSONArray a = result.getJSONArray("result");
            List<String> ret = new ArrayList<>();
            for(int i = 0; i < a.length(); i++) {
                ret.add(a.getJSONObject(i).getString("id"));
            }

            return ret;

        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("name \"" + name + "\" lead to Malformed URL", e);
        } catch (JSONException e) {
            throw new RuntimeException("server returned invalid data", e);
        }
    }

    public static void findUserWithNameInBackground(final String name,
                                                    final NameSeachDoneCallback nsdc,
                                                    final boolean onUIThread) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final List<String> ids = findUserWithName(name);
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                nsdc.usersFound(ids);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        nsdc.usersFound(ids);
                    }
                } catch (final IOException | RuntimeException e) {
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                nsdc.usersFailedToFind(e);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        nsdc.usersFailedToFind(e);
                    }
                }
            }
        }).start();
    }

    public void saveUser() throws IOException {
        try {
            URL url;
            if(id == null) {
                url = new URL("http://scavengr.meteor.com/users/");

                Map<String, String> requestMap = new HashMap<>();
                requestMap.put("name", getName());
                requestMap.put("facebook", getFacebookId());
                id = NetworkHelper.doRequest(url, "POST", true, requestMap).getString("_str");
                requestMap.clear();
                Log.d("ID_ID", id);
            }

            // Make an empty review array.
            url = new URL("http://scavenger.meteor.com/users/" + id + "/reviews");
            NetworkHelper.doRequest(url, "POST", true, new HashMap<String, String>());

            // Save the tasks
            url = new URL("http://scavengr.meteor.com/users/" + id + "/hunts");
            NetworkHelper.doRequest(url, "POST", true, new HashMap<String, String>());

        } catch(MalformedURLException ex) {
            throw new RuntimeException("bad url", ex);
        } catch (JSONException ex) {
            throw new RuntimeException("server returned bad data", ex);
        }
    }

    public void saveUserInBackground(final UserSavedCallback usc,
                                     final boolean onUIThread) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    saveUser();
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                usc.userSaved();
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        usc.userSaved();
                    }
                } catch (final IOException | RuntimeException e) {
                    if(onUIThread) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                usc.userFailedToSave(e);
                            }
                        };
                        new Handler(Looper.getMainLooper()).post(r);
                    } else {
                        usc.userFailedToSave(e);
                    }
                }
            }
        }).start();
    }

    public static interface UserLoadedCallback {

        /**
         * Called when a user is successfully loaded.
         * @param user The loaded user.
         */
        public void userLoaded(User user);

        /**
         * Called when a user fails to load. Also called if an exception
         * is thrown by userLoaded.
         * @param ex The exception thrown.
         */
        public void userFailedToLoad(Exception ex);

    }

    public static interface UserSavedCallback {

        public void userSaved();

        public void userFailedToSave(Exception ex);

    }


    public static interface NameSeachDoneCallback {

        public void usersFound(List<String> ids);

        public void usersFailedToFind(Exception ex);

    }

}
