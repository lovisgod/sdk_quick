package com.interswitchng.interswitchpossdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.interswitchng.interswitchpossdk.shared.PosUsbManager;

public class UsbActivity extends AppCompatActivity {

    private PosUsbManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb);

        manager = new PosUsbManager(this);
        manager.setUsbManagerErrorCallback(throwable -> {

            log("Error => " + throwable);
            return null;
        });

        manager.setUsbManagerMessageCallback(s -> {

            log("New Message => " + s);
            return null;
        });
    }

    private void log(String m) {
        Log.d("USB", m);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.connectAccessory(getIntent());
    }
}
