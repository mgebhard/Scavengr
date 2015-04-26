package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.util.HashMap;


public class HuntRecapActivity extends BaseActivity implements View.OnClickListener {

    private Hunt hunt;
    private User user;
    private HashMap<Task, Bitmap> images;
    private ImageSwitcher imageSwitcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_recap);
//        imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);

        if (getIntent().hasExtra("hunt")) {
            Log.d("HuntRecap", "Found hunt obj");
            hunt =  getIntent().getParcelableExtra("hunt");
            Log.d("HuntRecapActivity", hunt.toString());
        } else {
            Log.d("HuntRecapActivity", "This is failing hard");
        }

        if (getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");
        }
//        if (getIntent().hasExtra("photos")) {
//            images = (HashMap<Task, Bitmap>) getIntent().getParcelableExtra("photos");
//        }
//
//        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
//        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
//        imageSwitcher.setInAnimation(in);
//        imageSwitcher.setOutAnimation(out);
        Log.d("HuntRecap", hunt.toString());
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
                review.putExtra("huntObj", (Parcelable)hunt);
                Log.d("HuntRecap", user.toString());
                if (user != null) {
                    review.putExtra("user", user);
                }
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
