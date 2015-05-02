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

import com.parse.ParseAnalytics;

import org.teamscavengr.scavengr.BaseActivity;
import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.MainActivity;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.User;

import java.util.HashMap;
import java.util.Map;


public class ConfirmHuntActivity extends BaseActivity implements View.OnClickListener {
    private Hunt hunt;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hunt);
//        if (getIntent().hasExtra("huntObject")) {
        if (MainActivity.hunt != null) {
//            hunt = (getIntent().getParcelableExtra("huntObject"));
            hunt = MainActivity.hunt;
            // Grab and set hunt title
            TextView titleText = (TextView) findViewById(R.id.textView3);
            titleText.setText(hunt.getName());

            // Grab and set hunt description
            TextView descriptionText = (TextView) findViewById(R.id.textView4);
            descriptionText.setText("Description: " + hunt.getDescription());

            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            ratingBar.setRating(hunt.getRating());

        } else {
            Toast.makeText(this, "The cat is dead - failed to load data", Toast.LENGTH_SHORT).show();
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
                // Analytics
                Map<String, String> dims = new HashMap<>();
                dims.put("huntId", hunt.getId());
                if(currentUser != null)
                    dims.put("userId", currentUser.getId());
                ParseAnalytics.trackEventInBackground("start-hunt", dims);
                finish();
//                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
//                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent huntIntent = new Intent(this, HuntActivity.class);
                    huntIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    huntIntent.putExtra("huntObject", (Parcelable) hunt);
                    huntIntent.putExtra("user", currentUser);
                    this.startActivity(huntIntent);
//                } else {
//                    Toast toast = Toast.makeText(this,
//                            "Please enable location to go on a hunt!",
//                            Toast.LENGTH_LONG);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
//                }
                break;

            case R.id.back:
                Intent intent= new Intent(this, HuntsList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user", currentUser);
                this.startActivity(intent);
                this.finish();

            default:
                break;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
        onDestroy();
    }
}
