package org.teamscavengr.scavengr.createhunt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.teamscavengr.scavengr.Hunt;
import org.teamscavengr.scavengr.R;
import org.teamscavengr.scavengr.Task;
import org.teamscavengr.scavengr.User;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzhou1235 on 3/15/15.
 */
public class ReviewCreatedHunt extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_created_hunt);
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
                final StringBuilder huntName = new StringBuilder();
                final StringBuilder huntDesc = new StringBuilder();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.stuff, null))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int id) {
                                huntName.append(
                                        ((EditText) findViewById(R.id.huntName)).getText().toString());
                                huntDesc.append(
                                        ((EditText) findViewById(R.id.huntDescription)).getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();


                Set<Task> tasks = (Set<Task>) getIntent().getSerializableExtra("allTasks");
                long estimatedTime = getIntent().getLongExtra("estimatedTime", 0);
                TimeUnit unit = (TimeUnit) getIntent().getSerializableExtra("estimatedTimeUnit");
                User user = (User) getIntent().getSerializableExtra("currentUser");

                final Hunt h = new Hunt(null, huntName.toString(), new String[]{},
                        tasks.toArray(new Task[tasks.size()]), huntDesc.toString(), user.getId(),
                        estimatedTime, unit, System.currentTimeMillis() / 1000L);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            h.saveHunt();
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
            default:
                break;
        }

    }
}