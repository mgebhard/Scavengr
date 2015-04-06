package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.R;


public class RateHuntActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_hunt);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.submit_review:
                Intent recap = new Intent(this, HuntRecapActivity.class);
                this.startActivity(recap);
                break;
            default:
                break;
        }

    }
}
