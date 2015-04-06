package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;


public class HuntRecapActivity extends BaseActivity implements View.OnClickListener {
    private Hunt hunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_recap);
        if (getIntent().hasExtra("huntObj")){
            hunt =  getIntent().getParcelableExtra("huntObj");
        }

        String waypointText = "";
        for (Task task: hunt.getTasks()){
            waypointText += task.getAnswer() + "\n";
        }
        TextView waypoints = (TextView) findViewById(R.id.waypoints);
        waypoints.setText(waypointText);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.review:
                Intent hunt = new Intent(this, RateHuntActivity.class);
                this.startActivity(hunt);
                break;
            case R.id.home:
                Intent createHuntIntent = new Intent(this, MainActivity.class);
                this.startActivity(createHuntIntent);
                break;
            default:
                break;
        }

    }
}
