package org.teamscavengr.scavengr;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;

import org.teamscavengr.scavengr.createhunt.MyHuntsActivity;
import org.teamscavengr.scavengr.goonhunt.HuntsList;


public class MainActivity extends FragmentActivity implements View.OnClickListener{


    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";


    private static final int LOGIN = 0;
    private static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION +1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    public GoogleApiClient mGoogleApiClient;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent home;
        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.logout:
                LoginManager.getInstance().logOut();
                home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                return super.onOptionsItemSelected(item);
            case R.id.action_home:
                home = new Intent(this, MainActivity.class);
                this.startActivity(home);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
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

        switch(view.getId()) {
            case R.id.go_on_hunt:
                //Intent hunt = new Intent(this, HuntsList.class);
                Intent hunt = new Intent(this, HuntsList.class);
                // Pass in Geo Location of user
                this.startActivity(hunt);
                break;
            case R.id.create_hunt:
                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
                this.startActivity(createHuntIntent);
                break;

            default:
                break;
        }

    }

}
