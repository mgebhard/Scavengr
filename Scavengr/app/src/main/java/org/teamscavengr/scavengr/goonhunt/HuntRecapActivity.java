package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;


public class HuntRecapActivity extends BaseActivity implements View.OnClickListener {

    private Hunt hunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_recap);
        if (getIntent().hasExtra("huntObject")) {
            hunt = (getIntent().getParcelableExtra("huntObject"));
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.review:
                Intent review = new Intent(this, RateHuntActivity.class);
                review.putExtra("huntObj", (Parcelable)hunt);
                this.startActivity(review);
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
