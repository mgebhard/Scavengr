package org.teamscavengr.scavengr.goonhunt;

import android.app.ListActivity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class HuntsList extends ListActivity {

    // This is the Adapter being used to display the list's data cursor -> XML fields
    ArrayAdapter mAdapter;
    ArrayList<Hunt> mHuntsObj = new ArrayList<Hunt>();
    ArrayList<String> mHuntNames = new ArrayList<String>();

    // These are the Hunt rows that we will retrieve from DB
    //static final String[] PROJECTION = new String[] {HuntContract.Data._ID,
//            HuntContract.Data.DISPLAY_NAME};

    // This is the select criteria pass in geo location
//    static final String SELECTION = "((" +
//            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
//            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("listview", "\n\n\nCreating list view\n\n\n");
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);

        /*String[] values = new String[] { "Boston hunt 1", "Boston hunt 2", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values); */

        // For the cursor adapter, specify which columns go into which views
        // use static hunt objects for now
        /*String[] reviewId = {"12345643210"};
        Task[] tasks = new Task[1];
        try {
            tasks[0] = new Task(new JSONObject(""));
        } catch (JSONException e) {

        }
        Hunt basicHunt = new Hunt("basicHunt", "1234567890", reviewId, tasks);*/

        //load hunts as they come in
        ArrayList<Hunt> hunts = new ArrayList<Hunt>();
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
                       int duration = Toast.LENGTH_LONG;

                       Toast.makeText(HuntsList.this, "Loaded " + hunt.getId(), duration).show();
                       mHuntNames.add(hunt.getName());
                       mHuntsObj.add(hunt);
                       mAdapter.notifyDataSetChanged();
                   }

                   @Override
                   public void huntFailedToLoad(Exception e) {

                       //Context context = getApplicationContext();
                       //Context context = getApplicationContext();
                       CharSequence text = "Failed to load a hunt";

                       int duration = Toast.LENGTH_SHORT;
                       Toast.makeText(HuntsList.this, e.getMessage(), duration).show();
                   }
               }, true);
        //hunts.add(basicHunt);
        //mHuntNames.add("Loading Hunts");
        //String[] fromColumns = {hunts.get(0).getId() , hunts.get(0).getName()};
        //String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        // int[] toViews = {R.id.hunt_icon, R.id.hunt_label}; // The TextView in simple_list_item_1


        //TODO: REMOVE THIS; JUST FOR TESTING PURPOSES
        /*List<String> revIds = new ArrayList<String>();
        revIds.add("4.5");
        List<Task> tasks = new ArrayList<Task>();
        Task task = new Task(null, new Location("network"), "It's a cool place.",
        "Simmons", 30.0, 1);
        tasks.add(task);

        Hunt h = new Hunt(null, "FakeHunt", revIds, tasks, "The best hunt ever!!",
        "reallySmartRabbit", 2L, TimeUnit.HOURS, 20L);

        mHuntNames.add(h.getName());
        mHuntsObj.add(h); */

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, mHuntNames);
        setListAdapter(mAdapter);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        // getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Log.d("Null?", mHuntsObj.get(position).toString());
        Intent confirmGoingOnHunt = new Intent(this, ConfirmHuntActivity.class);
        confirmGoingOnHunt.putExtra("huntObject", (Parcelable) mHuntsObj.get(position));
        this.startActivity(confirmGoingOnHunt);
        // Put in ID for the hunt selected
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hunts_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
