package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;

import java.util.HashMap;


public class HuntRecapActivity extends BaseActivity implements View.OnClickListener {

    private Hunt hunt;
    private HashMap<Task, Bitmap> images;
    private ImageSwitcher imageSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_recap);
        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);

        if (getIntent().hasExtra("huntObj")) {
            hunt =  getIntent().getParcelableExtra("huntObj");
        }
        if (getIntent().hasExtra("photos")) {
            images = (HashMap<Task, Bitmap>) getIntent().getParcelableExtra("photos");
        }

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);

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
