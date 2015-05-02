package org.teamscavengr.scavengr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseCrashReporting;

import org.teamscavengr.scavengr.createhunt.MyHuntsActivity;
import org.teamscavengr.scavengr.goonhunt.HuntsList;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    public static final int LOGIN = 0;
    public static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION +1;
    public static Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    public static boolean waitingLogin = false;
    public static FragmentManager fm;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private boolean isResumed = false;
    public static User user = null;
    public static boolean loggedInSuccess = false;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (!ParseCrashReporting.isCrashReportingEnabled()) {
            ParseCrashReporting.enable(this);
        }
        Parse.initialize(this, "cMCitx9vmYz1tuypMXackoJING2zhrJN09qkkHuN",
                "viVgGhou3pDvH37gV8VuoWQw0jYGXVTHtNstWz4E");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        callbackManager = CallbackManager.Factory.create();
//        showFragment(LOGIN, true);
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile,
                                                       Profile currentProfile) {
//                if (loggedInSuccess) {
//                    FragmentManager manager = getSupportFragmentManager();
//                    int backStackSize = manager.getBackStackEntryCount();
//                    for (int i = 0; i < backStackSize; i++) {
//                        manager.popBackStack();
//                    }
//                    if (currentAccessToken != null) {
//                    showFragment(SELECTION, false);
//                    } else {
//                        showFragment(LOGIN, false);
//                    }

                    if(currentProfile == null) {
                        Log.d("Profile", "This shouldnt be null");
                        // Quick hack to fix some user being stuck being logged in without a user object
                        Intent home = new Intent(MainActivity.this ,MainActivity.class);
                        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(home);
                        return;
                    }
                    if (loggedInSuccess) {
                        // Dont bother trying to find user again.
                        return;
                    }
                    User.findUserWithFacebookIdInBackground(currentProfile.getId(),
                            new User.FacebookLookupDoneCallback() {
                                @Override
                                public void usersFound(List<String> ids) {
                                    if (ids.size() > 0) {
                                        User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
                                            @Override
                                            public void userLoaded(User user) {
                                                MainActivity.user = user;
                                                Map<String, String> dimensions = new HashMap<String, String>();
                                                dimensions.put("userId", user.getId());
                                                ParseAnalytics.trackEventInBackground("user-login", dimensions);
                                            }

                                            @Override
                                            public void userFailedToLoad(Exception ex) {
//                                                Toast.makeText(MainActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                                            }
                                        }, true);
                                    }
                                }

                                @Override
                                public void usersFailedToFind(Exception e) {
//                                    Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();
                                }
                            }, true);

                }
//            }
        };

//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
////                showFragment(LOGIN, false);
//                showFragment(SELECTION, true);
//                User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(),
//                    new User.FacebookLookupDoneCallback() {
//                        @Override
//                        public void usersFound(List<String> ids) {
//                            if (ids.size() > 0) {
//                                User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
//                                    @Override
//                                    public void userLoaded(User user) {
//                                        MainActivity.user = user;
//                                        Map<String, String> dimensions = new HashMap<String, String>();
//                                        dimensions.put("userId", user.getId());
//                                        ParseAnalytics.trackEventInBackground("user-login", dimensions);
//                                        MainActivity.loggedInSuccess = true;
//                                    }
//
//                                    @Override
//                                    public void userFailedToLoad(Exception ex) {
//                                        Toast.makeText(MainActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
//                                    }
//                                }, true);
//                            } else {
//                                Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//
//                        @Override
//                        public void usersFailedToFind(Exception e) {
//                            Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();
//                        }
//                    }, true);
//            }
//
//            @Override
//            public void onCancel() {
//                // Do nothing right now
//            }
//
//            @Override
//            public void onError(FacebookException e) {
//                // Do nothing right now
//            }
//        });


        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentById(R.id.loginFragment);

        fragments[LOGIN] = loginFragment;
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        // Start by displaying the login fragment
//        if (AccessToken.getCurrentAccessToken() != null || Profile.getCurrentProfile() != null) {
//            showFragment(SELECTION)
//        }
        showFragment(LOGIN, true);
        // Hide all fragments at first
//        FragmentTransaction transaction = fm.beginTransaction();
//        for (final Fragment fragment : fragments) {
//            transaction.hide(fragment);
//        }
//        transaction.hide(fragments[SELECTION]);
//        transaction.commit();
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public static void userSaved(User user) {
        MainActivity.user = user;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user == null && Profile.getCurrentProfile() != null) {
            User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(),
                    new User.FacebookLookupDoneCallback() {
                        @Override
                        public void usersFound(List<String> ids) {
                            if (ids.size() > 0) {
                                User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
                                    @Override
                                    public void userLoaded(User user) {
                                        MainActivity.user = user;
                                        Map<String, String> dimensions = new HashMap<String, String>();
                                        dimensions.put("userId", user.getId());
                                        ParseAnalytics.trackEventInBackground("user-login", dimensions);
                                        MainActivity.loggedInSuccess = true;
                                    }

                                    @Override
                                    public void userFailedToLoad(Exception ex) {
//                                        Toast.makeText(MainActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                                    }
                                }, true);
                            } else {
//                                Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void usersFailedToFind(Exception e) {
//                            Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();
                        }
                    }, true);
        }
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onResume methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (AccessToken.getCurrentAccessToken() != null) {
            // if the user already logged in, try to show the selection fragment
            showFragment(SELECTION, false);

        } else {
            // otherwise present the splash screen and ask the user to login,
            // unless the user explicitly skipped.
            showFragment(LOGIN, false);
        }
    }
//
//    public void showLoginFragment() {
//        showFragment(LOGIN, true);
//    }
//
//    public void startHomeScreenIntent () {
//        Intent home = new Intent(this, SelectionFragment.class);
//        // Pass in Geo Location of user
//        this.startActivity(home);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent home;
        switch (item.getItemId()) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
                waitingLogin = false;
                home = new Intent(this, MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(home);
                return super.onOptionsItemSelected(item);

            case R.id.action_home:
                home = new Intent(this, MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(home);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.go_on_hunt:
                Intent hunt = new Intent(this, HuntsList.class);
                if (user == null) {
                    if (Profile.getCurrentProfile().getId() != null) {
                        User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(), new User.FacebookLookupDoneCallback() {
                            @Override
                            public void usersFound(List<String> ids) {
                                if (ids.size() > 0) {
                                    User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
                                        @Override
                                        public void userLoaded(User user) {
                                            MainActivity.user = user;
                                            Map<String, String> dimensions = new HashMap<String, String>();
                                            dimensions.put("userId", user.getId());
                                            ParseAnalytics.trackEventInBackground("user-login", dimensions);
                                        }

                                        @Override
                                        public void userFailedToLoad(Exception ex) {
//                                            Toast.makeText(MainActivity.this, "Something went wrong. Try logging out and back in.", Toast.LENGTH_SHORT).show();
                                        }
                                    }, true);
                                }
                            }

                            @Override
                            public void usersFailedToFind(Exception e) {
//                                Toast.makeText(MainActivity.this, "Facebook Profile does not match user account", Toast.LENGTH_SHORT).show();
//                                User newUser = new User(null, Profile.getCurrentProfile().getName(), Optional.<String>empty(), Optional.of(Profile.getCurrentProfile().getId()));
//                                    user = newUser;
//                                    try {
//                                        newUser.saveUser();
//                                    } catch (IOException ex) {
//                                        Toast.makeText(MainActivity.this, "Failed to create new user", Toast.LENGTH_SHORT).show();
//                                    }
                            }
                        }, true);
                    }
//                    Toast.makeText(MainActivity.this, "User account not found yet", Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    hunt.putExtra("user", user);
                }
                // Pass in Geo Location of user
                this.startActivity(hunt);
                break;

            case R.id.create_hunt:
                if (user == null) {
                    if (Profile.getCurrentProfile().getId() != null) {
                        User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(), new User.FacebookLookupDoneCallback() {
                            @Override
                            public void usersFound(List<String> ids) {
                                if (ids.size() > 0) {
                                    User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
                                        @Override
                                        public void userLoaded(User user) {
                                            MainActivity.user = user;
                                            Map<String, String> dimensions = new HashMap<String, String>();
                                            dimensions.put("userId", user.getId());
                                            ParseAnalytics.trackEventInBackground("user-login", dimensions);
                                        }

                                        @Override
                                        public void userFailedToLoad(Exception ex) {
                                            Toast.makeText(MainActivity.this, "Something went wrong. Try logging out and back in.", Toast.LENGTH_SHORT).show();
                                        }
                                    }, true);
                                }
                            }

                            @Override
                            public void usersFailedToFind(Exception e) {
                                Toast.makeText(MainActivity.this, "Facebook Profile does not match user account", Toast.LENGTH_SHORT).show();
//                                User newUser = new User(null, Profile.getCurrentProfile().getName(), Optional.<String>empty(), Optional.of(Profile.getCurrentProfile().getId()));
//                                    user = newUser;
//                                    try {
//                                        newUser.saveUser();
//                                    } catch (IOException ex) {
//                                        Toast.makeText(MainActivity.this, "Failed to create new user", Toast.LENGTH_SHORT).show();
//                                    }
                            }
                        }, true);
                        Toast.makeText(MainActivity.this, "User account not found yet", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
                createHuntIntent.putExtra("user", user);
                this.startActivity(createHuntIntent);
                break;

            case R.id.skip_login_button:
                showFragment(SELECTION, false);
                break;

            default:
                break;
        }

    }

}
