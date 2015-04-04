package org.teamscavengr.scavengr.goonhunt;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.ServerGetHunt;
import org.teamscavengr.scavengr.Task;

import java.io.IOException;
import java.util.ArrayList;


public class HuntsList extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // This is the Adapter being used to display the list's data cursor -> XML fields
    SimpleCursorAdapter mAdapter;

    // These are the Hunt rows that we will retrieve from DB
//    static final String[] PROJECTION = new String[] {HuntContract.Data._ID,
//            HuntContract.Data.DISPLAY_NAME};

    // This is the select criteria pass in geo location
//    static final String SELECTION = "((" +
//            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
//            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
//
//        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);

        /*String[] values = new String[] { "Boston hunt 1", "Boston hunt 2", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        */


        // For the cursor adapter, specify which columns go into which views
        // use static hunt objects for now
        String[] reviewId = {"12345643210"};
        Task[] tasks = new Task[1];
        try {
            tasks[0] = new Task(new JSONObject(""));
        } catch (JSONException e) {

        }
        Hunt basicHunt = new Hunt("basicHunt", "1234567890", reviewId, tasks);
        ArrayList<Hunt> hunts = new ArrayList<Hunt>();
        Hunt.loadAllHuntsInBackground(
            new Hunt.HuntLoadedCallback() {

                   @Override
                   public void numHuntsFound(int num) {
                     
                   }

                   @Override
                   public void huntLoaded(Hunt hunt) {
                        //hunts
                   }

                   @Override
                   public void huntFailedToLoad(Exception e) {

                   }
               }, true);
        hunts.add(basicHunt);

        String[] fromColumns = {hunts[0].getId() , hunts[0].getName()};
        //String[] fromColumns = {ContactsContract.Data.DISPLAY_NAME};
        int[] toViews = {R.id.hunt_icon, R.id.hunt_label}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()
        mAdapter = new SimpleCursorAdapter(this,
                R.layout.hunt_list_row, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);


        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
//        getLoaderManager().initLoader(0, null, this);
    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
//        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
//                PROJECTION, SELECTION, null, null);
        return new CursorLoader(this);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Intent hunt = new Intent(this, ConfirmHunt.class);
        this.startActivity(hunt);
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
