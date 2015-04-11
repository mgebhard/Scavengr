package org.teamscavengr.scavengr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.ProfilePictureView;


public class MainActivity extends FragmentActivity {


    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";


    private static final int LOGIN = 0;
    private static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION +1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];



    //    private ProfilePictureView profilePictureView;
    //    private TextView greeting;
    //    private GoogleApiClient googleApiClient;
    //    private GeofenceManager manager;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private boolean userSkippedLogin = false;
    private boolean isResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (isResumed) {
                    FragmentManager manager = getSupportFragmentManager();
                    int backStackSize = manager.getBackStackEntryCount();
                    for (int i = 0; i < backStackSize; i++) {
                        manager.popBackStack();
                    }
                    if (currentAccessToken != null) {
                        showFragment(SELECTION, false);
                    } else {
                        showFragment(LOGIN, false);
                    }
                }
            }
        };

        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentById(R.id.loginFragment);

        fragments[LOGIN] = loginFragment;
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);

        // Hide all fragments at first
        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        loginFragment.setSkipLoginCallback(new LoginFragment.SkipLoginCallback() {
            @Override
            public void onSkipLoginPressed() {
                userSkippedLogin = true;
                showFragment(SELECTION, false);
            }
        });
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

//    private void updateUI() {
//        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
//
//        Profile profile = Profile.getCurrentProfile();
//        if (enableButtons && profile != null) {
//            profilePictureView.setProfileId(profile.getId());
//            greeting.setText(getString(R.string.hello_user, profile.getFirstName()));
//        } else {
//            profilePictureView.setProfileId(null);
//            greeting.setText(null);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        googleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (AccessToken.getCurrentAccessToken() != null) {
            userSkippedLogin = false;
            // if the user already logged in, try to show the selection fragment
            showFragment(SELECTION, false);

        } else if (userSkippedLogin) {
            showFragment(SELECTION, false);
        } else {
            // otherwise present the splash screen and ask the user to login,
            // unless the user explicitly skipped.
            showFragment(LOGIN, false);
        }
    }

    public void showLoginFragment() {
        showFragment(LOGIN, true);
    }

    public void startHomeScreenIntent () {
        Intent home = new Intent(this, SelectionFragment.class);
        // Pass in Geo Location of user
        this.startActivity(home);
    }





    //        buildApiClient();

//        FacebookSdk.sdkInitialize(getApplicationContext());

//        manager = new GeofenceManager(this, googleApiClient);

    //HuntActivity.dmlp = BaseActivity.dmlp;

        /*((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .requestLocationUpdates("network", 100L, 0.5f, new LocationListener() {

            @Override
            public void onLocationChanged(final Location location) {
                Toast.makeText(MainActivity.this, "Location changed: " + location.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(final String provider, final int status,
                                        final Bundle extras) {}

            @Override
            public void onProviderEnabled(final String provider) {}

            @Override
            public void onProviderDisabled(final String provider) {}
        });*/

//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions("user_friends");

//
//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // TODO(ZACH || EVER): Store the user in the database
//
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//            }
//        });
//
//
//        ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates("network", 10000L, 0.5f, new LocationListener() {
//            @Override
//            public void onLocationChanged(final Location location) {
//                Log.d("ZACH", location.toString());
//            }
//
//            @Override
//            public void onStatusChanged(final String provider, final int status,
//                                        final Bundle extras) {
//            }
//
//            @Override
//            public void onProviderEnabled(final String provider) {
//            }
//
//            @Override
//            public void onProviderDisabled(final String provider) {
//            }
//        });
//
//        // production build
//        if((getApplication().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
//            if(checkCallingOrSelfPermission("android.permission.ACCESS_MOCK_LOCATION") ==
//               PackageManager.PERMISSION_GRANTED) {
//                throw new RuntimeException("In production mode with mock location enabled, idiots!");
//            }
//        }

//    private synchronized void buildApiClient() {
//        googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        if(dmlp != null)
////            dmlp.close();
////        manager.removeGeofences();
//    }

//    public void onClick(View view) {
        /*Hunt.loadHuntInBackground("e4dbb85d17ea96e135b58a4a", new Hunt.HuntLoadedCallback() {
            @Override
            public void huntLoaded(final Hunt hunt) {
                Toast.makeText(MainActivity.this, "loaded hunt " + hunt.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void huntFailedToLoad(final Exception ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "failed to load hunts", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void numHuntsFound(final int i) {}

        }, true);*/

//        switch(view.getId()) {
//            case R.id.go_on_hunt:
//                //Intent hunt = new Intent(this, HuntsList.class);
//                Intent hunt = new Intent(this, HuntsList.class);
//                // Pass in Geo Location of user
//                this.startActivity(hunt);
//                break;
//            case R.id.create_hunt:
//                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
//                this.startActivity(createHuntIntent);
//                break;
//            case R.id.geofenceButton:
//                if(!googleApiClient.isConnected()) {
//                    Toast.makeText(this, "client not connected", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                Location l = new Location("");
//                l.setLatitude(42.358801d); // The coords of the stud
//                l.setLongitude(-71.094635d);
//                manager.addGeofence("geofenceStud", l, 100, Geofence.NEVER_EXPIRE, this,
//                        new GeofenceManager.GeofenceListener() {
//                            @Override
//                            public void geofenceTriggered(final GeofenceManager.GeofenceEvent event) {
//                                Toast.makeText(MainActivity.this,
//                                        event.geofenceId + ": " + ((event.type == GeofenceManager.GeofenceEvent.ENTERED_GEOFENCE) ? "entered" : "exited"),
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            default:
//                break;
//        }

//    }

//    @Override
//    public void onConnected(final Bundle bundle) {
//        Toast.makeText(this, "gapi client connected", Toast.LENGTH_SHORT).show();
//        LocationServices.FusedLocationApi.setMockMode(googleApiClient, true);
//        BaseActivity.dmlp = new DirectMockLocationProvider("network", this);
//    }
//
//    @Override
//    public void onConnectionSuspended(final int i) {
//        // Reconnect?
//        googleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(final ConnectionResult connectionResult) {
//        Toast.makeText(this, "gapi client connection failed", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onResult(final Status status) {
//        if(status.isSuccess()) {
//            Toast.makeText(this, "W00T", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Awwww... :(", Toast.LENGTH_SHORT).show();
//        }
//    }
}
