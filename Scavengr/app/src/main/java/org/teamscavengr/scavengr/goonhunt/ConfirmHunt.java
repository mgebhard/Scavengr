package org.teamscavengr.scavengr.goonhunt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;


public class ConfirmHunt extends ActionBarActivity implements View.OnClickListener {
    private Hunt hunt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hunt);
        if (getIntent().hasExtra("huntObject")) {
            Hunt hunt = (getIntent().getParcelableExtra("huntObject"));

            // Grab and set hunt title
            TextView titleText = (TextView) findViewById(R.id.textView3);
            titleText.setText(hunt.getName());

            // Grab and set hunt description
            TextView descriptionText = (TextView) findViewById(R.id.textView4);
            descriptionText.setText(hunt.getDescription());
        }
        else {
            Context context = getApplicationContext();
            CharSequence text = "The cat is dead - Failed to load data";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        /*    Context context = getApplicationContext();
            CharSequence text = "Im alive";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
        */
            //(getIntent().getParcelableExtra("huntObject"));

        //}*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.confirm_hunt:
                Intent hunt = new Intent(this, HuntActivity.class);
                hunt.putExtra("huntObject", (Parcelable) hunt);
                this.startActivity(hunt);
                break;
            default:
                break;
        }

    }
}
