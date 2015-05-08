package org.teamscavengr.scavengr.createhunt;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.CalcLib;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;
import org.teamscavengr.scavengr.goonhunt.HuntsAdapter;
import org.teamscavengr.scavengr.goonhunt.HuntsList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class MyHuntsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static RecyclerView mRecyclerView;
    private static MyHuntsAdapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;

    static ArrayList<Hunt> mHuntsObj;
    private User currentUser;
    private static int REQUEST_EXIT;
    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        setContentLayout();
        mHuntsObj = new ArrayList<>();
//        mHuntNames = new ArrayList<>();
        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

//        setContentView(R.layout.activity_go_on_hunt_list);
        // Load hunts
        loadHunts();

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

        setAdapter();
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
//            mHuntNames = null;
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


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public void loadHunts() {
        Hunt.loadUsersHuntsInBackground(new Hunt.HuntLoadedCallback() {
            @Override
            public void huntLoaded(Hunt hunt) {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Pair<LatLng, Double> pair = CalcLib.calculateCentroidAndRadius(hunt);
                int distance = (int) (CalcLib.distanceFromLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pair.first) - pair.second);
                hunt.setListViewDistance(distance);

//                mHuntNames.add(hunt.getName());
                mHuntsObj.add(hunt);
                Collections.sort(mHuntsObj, new DistanceComparator());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void huntFailedToLoad(Exception ex) {

            }

            @Override
            public void numHuntsFound(int i) {

            }
        }, true, currentUser);
    }

    public void setContentLayout() {
        setContentView(R.layout.activity_my_hunts_list);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mAdapter.notifyDataSetChanged();
        }
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
    public void onConnectionSuspended(int cause) {
        // just chill for right now
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // just chill for right now
    }

    public void setAdapter() {
        mAdapter = new MyHuntsAdapter(this, currentUser, mHuntsObj);
    }

    public void createNewHunt(View view) {
        Intent hunt = new Intent(this, CreateHuntActivity.class);
        hunt.putExtra("user", currentUser);
        // Make sure to clear this before all hell breaks loose
        MainActivity.hunt = null;
        this.startActivity(hunt);

    }
}

class DistanceComparator implements Comparator<Hunt> {
    @Override
    public int compare(Hunt o1, Hunt o2) {
        if (o1.getListViewDistance() != null || o2.getListViewDistance() != null) {
            return o1.getListViewDistance().compareTo(o2.getListViewDistance());
        } else {
            return -1;
        }
    }
}