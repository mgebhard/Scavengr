package org.teamscavengr.scavengr.goonhunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Review;
import org.teamscavengr.scavengr.User;


public class RateHuntActivity extends BaseActivity implements View.OnClickListener {

    private User user;
    private Hunt hunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_hunt);
        user = getIntent().getParcelableExtra("user");
        hunt = getIntent().getParcelableExtra("hunt");
        TextView reviewHuntTitle = (TextView) findViewById(R.id.reviewHuntTitle);
        reviewHuntTitle.setText(hunt.getName());
        TextView reviewComments = (TextView) findViewById(R.id.reviewComments);
        reviewComments.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.submit_review:
                Intent recap = new Intent(this, HuntRecapActivity.class);
                recap.putExtra("hunt", (Parcelable) hunt);
                recap.putExtra("user", user);

                RatingBar reviewRating = (RatingBar) findViewById(R.id.reviewRatingBar);
                TextView reviewComments = (TextView) findViewById(R.id.reviewComments);

                Review currentReview = new Review(null, user.getId(),
                        reviewRating.getRating(), reviewComments.getText().toString(), hunt.getId());


                // save the review to the DB
                currentReview.saveReviewInBackground(true, new Review.ReviewSavedCallback() {
                    @Override
                    public void reviewSaved() {
                        Toast.makeText(RateHuntActivity.this, "Review saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void reviewFailedToSave(final Exception ex) {
                        Toast.makeText(RateHuntActivity.this, "Review not saved :(", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                });

                this.startActivity(recap);
                break;

            default:
                break;
        }

    }
}
