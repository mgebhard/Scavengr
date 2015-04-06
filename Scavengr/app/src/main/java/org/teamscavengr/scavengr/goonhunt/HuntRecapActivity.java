package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.review:
                Intent review = new Intent(this, RateHuntActivity.class);
                review.putExtra("huntObject", (Parcelable)hunt);
                this.startActivity(review);
                break;
            case R.id.home:
                Intent createHuntIntent = new Intent(this, MainActivity.class);
                createHuntIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(createHuntIntent);
                finish();
                break;
            default:
                break;
        }

    }
}
