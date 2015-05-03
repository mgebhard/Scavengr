package org.teamscavengr.scavengr.goonhunt;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;

import java.util.ArrayList;


public class HuntsList extends Activity{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Hunt> mHuntsObj = new ArrayList<>();
    ArrayList<String> mHuntNames = new ArrayList<>();
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_on_hunt_list);
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
        mRecyclerView.setHasFixedSize(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent home;
        switch (item.getItemId()) {
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

        return true;
    }


}
