package org.teamscavengr.scavengr.createhunt;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.Profile;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Activity to review a hunt, once created.
 *
 * Created by hzhou1235 on 3/15/15.
 */
public class ReviewCreatedHuntActivity extends ActionBarActivity implements View.OnClickListener {
    Hunt currentHunt;
    private int hour, minute;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_created_hunt);
        currentHunt = getIntent().getParcelableExtra("currentHunt");

        listView = (ListView) findViewById(R.id.list);
        List<String> values = new ArrayList<String>();
        for (Task task: currentHunt.getTasks()) {
            values.add("Task Number: " + task.getTaskNumber() +
                    " Task Location: " + task.getAnswer());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_hunt, menu);
        //TODO: load in waypoints and display
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
            case R.id.confirm:
                User user = (User) getIntent().getSerializableExtra("currentUser");
                currentHunt.setName(((EditText)findViewById(R.id.spoof_latitude)).getText().toString());
                currentHunt.setDescription(((EditText)findViewById(R.id.spoof_longitude)).getText().toString());
                currentHunt.setCreatorId(user.getId());
                currentHunt.setTimeCreated(System.currentTimeMillis() / 1000L);
                StringBuilder toastString = new StringBuilder();
                if (!currentHunt.checkHunt(toastString)) {
                    Toast.makeText(ReviewCreatedHuntActivity.this, toastString.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // TODO: remove dependency on facebook profile
                            currentHunt.saveHunt(Profile.getCurrentProfile().getId());

                            Log.d("SCV", "saveHunt returned");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Intent myHunts = new Intent(this, MyHuntsActivity.class);
                this.startActivity(myHunts);
                break;

            case R.id.back:
                this.finish(); //not sure if this works/keeps old stuff
                break;

            case R.id.estimated_time:
                EditText t = (EditText)view;
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReviewCreatedHuntActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(final TimePicker view, final int hourOfDay,
                                                  final int minute) {
                                ReviewCreatedHuntActivity.this.hour = hourOfDay;
                                ReviewCreatedHuntActivity.this.minute = minute;
                                ((EditText) findViewById(R.id.estimated_time)).setText(hourOfDay +
                                        ":" + String.format("%02d", minute));
                            }
                        }, 0, 0, true);
                timePickerDialog.setTitle("Enter estimated length");
                timePickerDialog.show();
                currentHunt.setEstimatedTime(hour * 60L + minute, TimeUnit.MINUTES);

            default:
                break;
        }

    }
}