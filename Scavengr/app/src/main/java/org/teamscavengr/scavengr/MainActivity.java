package org.teamscavengr.scavengr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.createhunt.MyHuntsActivity;
import org.teamscavengr.scavengr.goonhunt.HuntsList;

import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private static final int LOGIN = 0;
    private static final int SELECTION = 1;
    private static final int FRAGMENT_COUNT = SELECTION +1;
    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private boolean isResumed = false;
    private static User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

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

                    User.findUserWithFacebookIdInBackground(Profile.getCurrentProfile().getId(),
                            new User.FacebookLookupDoneCallback() {
                                @Override
                                public void usersFound(List<String> ids) {
                                    if (ids.size() > 0) {
                                        User.loadUserInBackground(ids.get(0), new User.UserLoadedCallback() {
                                            @Override
                                            public void userLoaded(User user) {
                                                MainActivity.user = user;
                                            }

                                            @Override
                                            public void userFailedToLoad(Exception ex) {
                                                Toast.makeText(MainActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                                            }

                                            }, true);
                                    }
                                }

                                @Override
                                public void usersFailedToFind(Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to find user with Facebook ID", Toast.LENGTH_SHORT).show();
                                }
                            }, true);

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
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent home;
        switch (item.getItemId()) {
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
        switch(view.getId()) {
            case R.id.go_on_hunt:
                if (user == null) {
                    Toast.makeText(MainActivity.this, "User account not found yet", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent hunt = new Intent(this, HuntsList.class);
                hunt.putExtra("user", user);
                // Pass in Geo Location of user
                this.startActivity(hunt);
                break;

            case R.id.create_hunt:
                if (user == null) {
                    Toast.makeText(MainActivity.this, "User account not found yet", Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent createHuntIntent = new Intent(this, MyHuntsActivity.class);
                createHuntIntent.putExtra("user", user);
                this.startActivity(createHuntIntent);
                break;

            default:
                break;
        }

    }

}
