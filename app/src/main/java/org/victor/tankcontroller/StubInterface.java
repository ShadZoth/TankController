package org.victor.tankcontroller;

import android.widget.Toast;

public class StubInterface implements TankInterface {
    private final MainActivity mActivity;

    public StubInterface(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void connect(String ip, String port) {
        toasty("Connecting to " + ip + " (" + port + ")");
    }

    private void toasty(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }
}
