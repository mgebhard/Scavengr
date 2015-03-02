package org.teamscavengr.scavengr;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button hunt = (Button)findViewById(R.id.hunt);
        Button create = (Button)findViewById(R.id.create_hunt);
        hunt.setOnClickListener(this);
        create.setOnClickListener(this);
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
            case R.id.hunt:
                Intent hunt = new Intent(this, MapTestActivity.class);
                this.startActivity(hunt);
                break;
            case R.id.create_hunt:
                Intent createHuntIntent = new Intent(this, CreateHuntActivity.class);
                this.startActivity(createHuntIntent);
                break;
            default:
                break;
        }

    }
}
