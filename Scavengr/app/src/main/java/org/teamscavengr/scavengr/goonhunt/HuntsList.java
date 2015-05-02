package org.teamscavengr.scavengr.goonhunt;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;

import java.util.ArrayList;


public class HuntsList extends ListActivity {

    static ArrayAdapter<String> mAdapter;
    static ArrayList<Hunt> mHuntsObj;
    static ArrayList<String> mHuntNames;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);
        mHuntsObj = new ArrayList<>();
        mHuntNames = new ArrayList<>();
        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }

        Hunt.loadAllHuntsInBackground(
            new Hunt.HuntLoadedCallback() {
                   @Override
                   public void numHuntsFound(int num) {
//                       CharSequence text = "Loading " + num + " hunts...";
//                       int duration = Toast.LENGTH_LONG;
//
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

        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mHuntNames);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent confirmGoingOnHunt = new Intent(this, ConfirmHuntActivity.class);
//        confirmGoingOnHunt.putExtra("huntObject", (Parcelable) mHuntsObj.get(position));
        MainActivity.hunt = mHuntsObj.get(position);
        Log.d("HuntList", currentUser.toString());
        confirmGoingOnHunt.putExtra("user", currentUser);
        this.startActivity(confirmGoingOnHunt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onTrimMemory(int trimStatus) {
        mHuntsObj = null;
        mAdapter = null;
        mHuntNames = null;
        finish();
        onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
//        finish();
//        onDestroy();
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

        return true;
    }


}
