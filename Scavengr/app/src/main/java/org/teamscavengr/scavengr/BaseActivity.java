package org.teamscavengr.scavengr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.teamscavengr.scavengr.mocklocation.DirectMockLocationProvider;

/**
 * All activities that need the spoof location button should inherit from this.
 *
 * Created by zrneely on 4/5/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected static DirectMockLocationProvider dmlp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.change_location:
                if(dmlp == null) {
                    Log.e("SCV", "dmlp is null!");
                }

                AlertDialog.Builder b = new AlertDialog.Builder(BaseActivity.this);
                b.setTitle("Location?");
                LayoutInflater inflater = getLayoutInflater();
                View v = inflater.inflate(R.layout.stuff, null);
                final EditText lat = (EditText) v.findViewById(R.id.spoof_latitude);
                final EditText lon = (EditText) v.findViewById(R.id.spoof_longitude);

                b.setView(v);
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                });
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        try {
                            double la = Double.parseDouble(lat.getText().toString());
                            double lo = Double.parseDouble(lon.getText().toString());
                            dmlp.setLocation(la, lo);
                            dmlp.update();
                        } catch(NumberFormatException ex) {
                            ex.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }
                    }
                });
                b.show();
                break;
            default:
                break;
        }

        return true;
    }


}
