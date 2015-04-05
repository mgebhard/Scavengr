package org.teamscavengr.scavengr.createhunt;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import org.teamscavengr.scavengr.HuntDetailsActivity;
import org.teamscavengr.scavengr.R;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class MyHuntsActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_hunts);
        //Button create = (Button) findViewById(R.id.create_new);
        //create.setOnClickListener(this);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);
        //
//        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_activity_view);

        String[] values = new String[] {"+ CREATE NEW HUNT", "My First Hunt", "My Second Hunt", "My Third Hunt",
                "BlahBlah", "BlahBlahBlah", "asdf", "jkl;", "anothername",
                "anothername2"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
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
        Intent hunt = null;

        if (position == 0){
            hunt = new Intent(this, CreateHuntActivity.class);
        } else {
            hunt = new Intent(this, HuntDetailsActivity.class);
        }
        this.startActivity(hunt);
        // Put in ID for the hunt selected
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review_hunt, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void onClick(View view) {
        switch (view.getId()) {
            //case R.id.create_new:
            //    Intent create = new Intent(this, CreateHuntActivity.class);
            //    this.startActivity(create);
            //    break;
            default:
                break;
        }
    }
}
