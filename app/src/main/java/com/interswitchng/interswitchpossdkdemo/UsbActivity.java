package com.interswitchng.interswitchpossdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class UsbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
