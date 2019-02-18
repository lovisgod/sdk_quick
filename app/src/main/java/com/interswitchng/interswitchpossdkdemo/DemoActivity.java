package com.interswitchng.interswitchpossdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.igweze.ebi.paxemvcontact.services.POSDeviceService;
import com.interswitchng.interswitchpossdk.IswPos;


public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        configureTerminal();
        setupUI();
    }

    private void configureTerminal() {
        POSDeviceService deviceService = POSDeviceService.create(getApplicationContext());
        // configure terminal
        IswPos.configureTerminal(getApplication(), deviceService);
    }

    private void setupUI() {
        final EditText amount = findViewById(R.id.amount);
        final AppCompatButton button = findViewById(R.id.btnSubmitAmount);

        button.setOnClickListener(v -> {
            String enteredAmount = amount.getText().toString();
            if (enteredAmount.isEmpty()) {
                Toast.makeText(DemoActivity.this, "Amount value is required", Toast.LENGTH_LONG).show();
            } else {
                // trigger payment
                IswPos.getInstance().initiatePayment(Integer.valueOf(enteredAmount));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.terminal_config) {
            IswPos.showSettingsScreen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}