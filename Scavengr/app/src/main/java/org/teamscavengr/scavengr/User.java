package org.teamscavengr.scavengr;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
 * A User is a user.
 */
public class User {

    private final String name;
    private final Optional<String> gPlusId;
    private final Optional<String> fbId;
    private final String email;
    private final String id;

    private User(String name, Optional<String> gPlusId, Optional<String> fbId, String email, String id) {
        this.name = name;
        this.gPlusId = gPlusId;
        this.id = id;
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
        InputStream in = null;
        try {
            // Initialize connection
            URL url = new URL("http://scavengr.meteor.com/users/" + id);
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

            return new User(result.getString("name"), gPlus, fb, result.getString("email"), id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("id \"" + id + "\" lead to Malformed URL", e);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("server returned invalid data");
        } finally {
            if(in != null) {
                in.close();
            }
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
                        new Handler(Looper.myLooper()).post(r);
                    } else {
                        ulc.userFailedToLoad(e);
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

}
