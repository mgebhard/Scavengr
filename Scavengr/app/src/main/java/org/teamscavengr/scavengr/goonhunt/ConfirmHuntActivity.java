package org.teamscavengr.scavengr.goonhunt;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;


public class ConfirmHuntActivity extends BaseActivity implements View.OnClickListener {
    private Hunt hunt;
    private User currentUser;

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
            descriptionText.setText("Description: " + hunt.getDescription());

            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating(hunt.getRating());

        } else {
            Context context = getApplicationContext();
            CharSequence text = "The cat is dead - Failed to load data";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

        if (getIntent().hasExtra("user")) {
            currentUser = getIntent().getParcelableExtra("user");
        }
        // Default image for hunt
        ImageButton image = (ImageButton) (findViewById(R.id.imageButton));
        image.setBackgroundResource(R.drawable.treasuremap);

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.confirm_hunt:
                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
                    Intent huntIntent = new Intent(this, HuntActivity.class);
                    huntIntent.putExtra("huntObject", (Parcelable) hunt);
                    huntIntent.putExtra("user", currentUser);
                    Log.d("NULL?", hunt.getName());
                    this.startActivity(huntIntent);
                } else {
                    Toast toast = Toast.makeText(this,
                            "Sorry but you are not allowed to go on a hunt until you turn location on.",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                break;

            case R.id.back:
                this.finish();

            default:
                break;
        }

    }

}
