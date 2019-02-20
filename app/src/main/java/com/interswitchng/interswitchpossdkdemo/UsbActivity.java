package com.interswitchng.interswitchpossdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.interswitchng.smartpos.usb.UsbConnectionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsbActivity extends AppCompatActivity {

    private UsbConnectionManager usbConnectionManager;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usbConnectionManager = new UsbConnectionManager();
        executorService.execute(() -> {

            boolean opened = usbConnectionManager.open();
            log("Opened => " + opened);
            while (true) {
                String data = usbConnectionManager.receive();
                log(data);

                usbConnectionManager.send("Received => " + data);

                if (data.equalsIgnoreCase("writing 4")) {
                    break;
                }
            }
        });

        setContentView(R.layout.activity_demo);

    }

    private void log(String m) {
        Log.d("USBTest", m);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
