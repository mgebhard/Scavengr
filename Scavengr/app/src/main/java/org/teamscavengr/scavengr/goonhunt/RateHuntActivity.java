package org.teamscavengr.scavengr.goonhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Review;


public class RateHuntActivity extends BaseActivity implements View.OnClickListener {

    private Hunt hunt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_hunt);
        if (getIntent().hasExtra("huntObject")) {
            hunt = (getIntent().getParcelableExtra("huntObject"));
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.submit_review:
                Intent recap = new Intent(this, HuntRecapActivity.class);
                RatingBar reviewRating = (RatingBar) findViewById(R.id.reviewRatingBar);
                TextView reviewComments = (TextView) findViewById(R.id.reviewComments);

                Review currentReview = new Review(null, hunt.getId(),
                        reviewRating.getRating(), reviewComments.getText().toString());

                // TODO(ZACH || EVER): save the review to the DB

                this.startActivity(recap);
                break;

            default:
                break;
        }

    }
}
