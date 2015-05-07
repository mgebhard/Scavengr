package org.teamscavengr.scavengr.goonhunt;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;

import java.util.ArrayList;


public class HuntsList extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static RecyclerView mRecyclerView;
    private static HuntsAdapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;

//    static ArrayAdapter<String> mAdapter;
    static ArrayList<Hunt> mHuntsObj;
    static ArrayList<String> mHuntNames;
    private User currentUser;
    private static int REQUEST_EXIT;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        setContentView(R.layout.activity_go_on_hunt_list);
        mHuntsObj = new ArrayList<>();
        mHuntNames = new ArrayList<>();
        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

//        setContentView(R.layout.activity_go_on_hunt_list);

        Hunt.loadAllHuntsInBackground(
                new Hunt.HuntLoadedCallback() {
                    @Override
                    public void numHuntsFound(int num) {
//                       CharSequence text = "Loading " + num + " hunts...";
//                       int duration = Toast.LENGTH_LONG;
//                       Toast.makeText(HuntsList.this, text, duration).show();
                    }

                    @Override
                    public void huntLoaded(Hunt hunt) {
                       findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                        mHuntNames.add(hunt.getName());
                        mHuntsObj.add(hunt);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void huntFailedToLoad(Exception e) {
//                       CharSequence text = "Failed to load a hunt";
//                       int duration = Toast.LENGTH_SHORT;
//                       Toast.makeText(HuntsList.this, text, duration).show();
                    }
                }, true);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

        mAdapter = new HuntsAdapter(this, mHuntNames, currentUser, mHuntsObj);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onTrimMemory(int trimStatus) {
        if (trimStatus == TRIM_MEMORY_COMPLETE || trimStatus == TRIM_MEMORY_MODERATE) {
            mHuntsObj = null;
            mAdapter = null;
            mHuntNames = null;
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        mGoogleApiClient.disconnect();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
//        finish();
//        onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mGoogleApiClient.disconnect();
//        finish();
//        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent home;
        switch (id) {
            case R.id.logout:
                LoginManager.getInstance().logOut();
                home = new Intent(this, MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.waitingLogin = false;
                this.startActivity(home);
                this.onDestroy();
                return super.onOptionsItemSelected(item);
            case R.id.action_home:
                home = new Intent(this, MainActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(home);
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                this.finish();

            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mAdapter.notifyDataSetChanged();
            Toast.makeText(this, mLastLocation.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Location set", mLastLocation.toString());
        }
        Log.d("Location set", "mLastLocation = null");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    @Override
    public void onConnectionSuspended(int cause) {
        // just chill for right now
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // just chill for right now
    }
}
