package org.teamscavengr.scavengr.goonhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;


public class ConfirmHuntActivity extends BaseActivity implements View.OnClickListener {
    private Hunt hunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hunt);
        if (getIntent().hasExtra("huntObject")) {
            hunt = (getIntent().getParcelableExtra("huntObject"));

            // Grab and set hunt title
            TextView titleText = (TextView) findViewById(R.id.textView3);
            titleText.setText(hunt.getName());

            // Grab and set hunt description
            TextView descriptionText = (TextView) findViewById(R.id.textView4);
            descriptionText.setText(hunt.getDescription());

            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating(hunt.getRating());

        } else {
            Context context = getApplicationContext();
            CharSequence text = "The cat is dead - Failed to load data";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        // Default image for hunt
        ImageButton image = (ImageButton) (findViewById(R.id.imageButton));
        image.setBackgroundResource(R.drawable.treasuremap);

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.confirm_hunt:
                Intent huntIntent = new Intent(this, HuntActivity.class);
                huntIntent.putExtra("huntObject", (Parcelable) hunt);
                Log.d("NULL?", hunt.getName());
                this.startActivity(huntIntent);
                break;

            case R.id.back:
                this.finish();

            default:
                break;
        }

    }

}