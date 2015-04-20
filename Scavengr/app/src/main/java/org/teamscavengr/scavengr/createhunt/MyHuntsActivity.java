package org.teamscavengr.scavengr.createhunt;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;

import java.util.ArrayList;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class MyHuntsActivity extends ListActivity implements
        View.OnClickListener {

    ArrayAdapter mAdapter;
    ArrayList<Hunt> mHuntsObj = new ArrayList<Hunt>();
    ArrayList<String> mHuntNames = new ArrayList<String>();
    ArrayList<String> huntsExtended = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("listview", "\n\n\nCreating list view\n\n\n");
        //super.onCreate(savedInstanceState);

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
        //setContentView(R.layout.custom_list_activity_view);

        //ArrayList<Hunt> hunts = new ArrayList<Hunt>();
        //huntsExtended.add("+ CREATE NEW HUNT");
        /*Hunt.loadAllHuntsInBackground(
                new Hunt.HuntLoadedCallback() {

                    @Override
                    public void numHuntsFound(int num) {
                        Context context = getApplicationContext();
                        CharSequence text = "Loading " + num + " hunts...";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(context, text, duration);
                    }

                    @Override
                    public void huntLoaded(Hunt hunt) {
                        mHuntNames.add(hunt.getName());
                        huntsExtended.add(hunt.getName());
                        mHuntsObj.add(hunt);
                        mAdapter.notifyDataSetChanged();
                        Log.d("HELEN", "LOADED");

                    }

                    @Override
                    public void huntFailedToLoad(Exception e) {
                        Context context = getApplicationContext();
                        CharSequence text = "Failed to load a hunt";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                    }
                }, true);*/

        //int[] toViews = {R.id.hunt_icon, R.id.hunt_label}; // The TextView in simple_list_item_1

        // Create an empty adapter we will use to display the loaded data.
        // We pass null for the cursor, then update it in onLoadFinished()

        /*mAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, huntsExtended);
        setListAdapter(mAdapter);*/
//
//        String[] values = new String[] {"+ CREATE NEW HUNT", "My First Hunt", "My Second Hunt", "My Third Hunt",
//                "BlahBlah", "BlahBlahBlah", "asdf", "jkl;", "anothername",
//                "anothername2"};
//
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, values);
//        setListAdapter(adapter);

        getActionBar().setIcon(R.drawable.scavengr_logo);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHuntsObj = new ArrayList<Hunt>();
        mHuntNames = new ArrayList<String>();
        huntsExtended = new ArrayList<String>();

        huntsExtended.add("+ CREATE NEW HUNT");
        setContentView(R.layout.custom_list_activity_view);
        Hunt.loadAllHuntsInBackground(
                new Hunt.HuntLoadedCallback() {

                    @Override
                    public void numHuntsFound(int num) {
                        Context context = getApplicationContext();
                        CharSequence text = "Loading " + num + " hunts...";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                    @Override
                    public void huntLoaded(Hunt hunt) {
                        mHuntNames.add(hunt.getName());
                        huntsExtended.add(hunt.getName());
                        mHuntsObj.add(hunt);
                        mAdapter.notifyDataSetChanged();
                        //Log.d("HELEN", "LOADED");

                    }

                    @Override
                    public void huntFailedToLoad(Exception e) {
                        Context context = getApplicationContext();
                        CharSequence text = "Failed to load a hunt";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }, true);

        mAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, huntsExtended);

        setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getActionBar().setIcon(R.drawable.scavengr_logo);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Intent hunt = null;

        if (position == 0){
            hunt = new Intent(this, CreateHuntActivity.class);
        } else {
            Log.d("HELEN", "GOIN TO HUNT DETAILS");
            hunt = new Intent(this, HuntDetailsActivity.class);
            hunt.putExtra("huntObject", (Parcelable) mHuntsObj.get(position - 1));
            //this.startActivity(hunt);
        }
        this.startActivity(hunt);
        // Put in ID for the hunt selected
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
