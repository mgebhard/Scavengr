package org.teamscavengr.scavengr.goonhunt;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HuntsList extends ListActivity {

    ArrayAdapter mAdapter;
    ArrayList<Hunt> mHuntsObj = new ArrayList<Hunt>();
    ArrayList<String> mHuntNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);

        Hunt.loadAllHuntsInBackground(
            new Hunt.HuntLoadedCallback() {
                   @Override
                   public void numHuntsFound(int num) {
                       CharSequence text = "Loading " + num + " hunts...";
                       int duration = Toast.LENGTH_LONG;

                       Toast.makeText(HuntsList.this, text, duration).show();
                   }

                   @Override
                   public void huntLoaded(Hunt hunt) {
                       findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                       int duration = Toast.LENGTH_LONG;

                       //Toast.makeText(HuntsList.this, "Loaded " + hunt.getId(), duration).show();
                       mHuntNames.add(hunt.getName());
                       mHuntsObj.add(hunt);
                       mAdapter.notifyDataSetChanged();
                   }

                   @Override
                   public void huntFailedToLoad(Exception e) {
                       CharSequence text = "Failed to load a hunt";
                       int duration = Toast.LENGTH_SHORT;
                       Toast.makeText(HuntsList.this, text, duration).show();
                       //Log.d("JSONError", e.toString());
                       //Toast.makeText(HuntsList.this, e.getMessage(), duration).show();
                   }
               }, true);

        mAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, mHuntNames);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {;
        Intent confirmGoingOnHunt = new Intent(this, ConfirmHuntActivity.class);
        confirmGoingOnHunt.putExtra("huntObject", (Parcelable) mHuntsObj.get(position));
        this.startActivity(confirmGoingOnHunt);
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
