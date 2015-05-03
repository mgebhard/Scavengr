package org.teamscavengr.scavengr.createhunt;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;

import java.util.ArrayList;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class MyHuntsActivity extends ListActivity {

    ArrayAdapter mAdapter;
    ArrayList<Hunt> mHuntsObj = new ArrayList<Hunt>();
    ArrayList<String> mHuntNames = new ArrayList<String>();
    ArrayList<String> huntsExtended = new ArrayList<String>();
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.scavengr_logo);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHuntsObj = new ArrayList<Hunt>();
        mHuntNames = new ArrayList<String>();
        huntsExtended = new ArrayList<String>();
        currentUser = (User) getIntent().getParcelableExtra("user");

        huntsExtended.add("+ CREATE NEW HUNT");
        setContentView(R.layout.custom_list_activity_view);
        Hunt.loadUsersHuntsInBackground(
                new Hunt.HuntLoadedCallback() {
                    @Override
                    public void numHuntsFound(int num) {
                        Context context = getApplicationContext();
//                        CharSequence text = "Loading " + num + " hunts...";
//                        int duration = Toast.LENGTH_SHORT;
                        if (num < 1) {
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
                    }

                    @Override
                    public void huntLoaded(Hunt hunt) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        mHuntNames.add(hunt.getName());
                        huntsExtended.add(hunt.getName());
                        mHuntsObj.add(hunt);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void huntFailedToLoad(Exception e) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
//                        Context context = getApplicationContext();
//                        CharSequence text = "Failed to load a hunt";
//                        int duration = Toast.LENGTH_SHORT;
//                        Toast toast = Toast.makeText(context, text, duration);
//                        toast.show();
                    }
                }, true, currentUser);

        mAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, huntsExtended);

        setListAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        getActionBar().setIcon(R.drawable.scavengr_logo);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent hunt = null;
        if (position == 0){
            hunt = new Intent(this, CreateHuntActivity.class);

        } else {
            hunt = new Intent(this, HuntDetailsActivity.class);
            hunt.putExtra("huntObject", (Parcelable) mHuntsObj.get(position - 1));
        }
        hunt.putExtra("user", currentUser);
        this.startActivity(hunt);
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
}
