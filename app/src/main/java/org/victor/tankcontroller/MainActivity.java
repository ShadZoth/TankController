package org.victor.tankcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

    private static final int RESULT_SETTINGS = 1;
    private TankInterface mTankInterface = new StubInterface(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTankInterface.connect(getIP(), getPort());
            }
        });
    }

    private String getIP() {
        //TODO: implement
        return SettingsActivity.DEFAULT_IP;
    }

    private String getPort() {
        //TODO: implement
        return SettingsActivity.DEFAULT_PORT;
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
            showSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Log.d("SETTINGS", "show");
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, RESULT_SETTINGS);
    }
}
