package org.teamscavengr.scavengr.createhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class ReviewCreatedHunt extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_created_hunt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_hunt, menu);
        //TODO: load in waypoints and display
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

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.confirm:
                Set<Task> tasks = (Set<Task>) getIntent().getSerializableExtra("allTasks");
                long estimatedTime = getIntent().getLongExtra("estimatedTime", 0);
                TimeUnit unit = (TimeUnit) getIntent().getSerializableExtra("estimatedTimeUnit");

                // Hunt h = new Hunt();

                Intent myHunts = new Intent(this, MyHuntsActivity.class);
                this.startActivity(myHunts);
                break;
            case R.id.back:
                this.finish(); //not sure if this works/keeps old stuff
                break;
            default:
                break;
        }

    }
}