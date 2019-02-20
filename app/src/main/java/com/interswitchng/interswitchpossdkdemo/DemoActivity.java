package com.interswitchng.interswitchpossdkdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.interswitchng.interswitchpossdk.IswPos;
import com.interswitchng.interswitchpossdk.shared.errors.NotConfiguredException;
import com.interswitchng.interswitchpossdk.shared.models.core.POSConfig;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService;


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
        POSConfig config = new POSConfig("MX5882");
        // configure terminal
        IswPos.configureTerminal(getApplication(), deviceService, config);
    }

    private void setupUI() {
        final EditText amount = findViewById(R.id.amount);
        final AppCompatButton button = findViewById(R.id.btnSubmitAmount);

        button.setOnClickListener(v -> {
            String enteredAmount = amount.getText().toString();
            if (enteredAmount.isEmpty()) {
                Toast.makeText(DemoActivity.this, "Amount value is required", Toast.LENGTH_LONG).show();
            } else {
                try {
                    // trigger payment
                    IswPos.getInstance().initiatePayment(Integer.valueOf(enteredAmount) * 100);
                } catch (NotConfiguredException e) {
                    Log.d("DEMO", e.getMessage());
                }
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
            IswPos.showSettingsScreen(); // show settings for terminal configuration
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // handle success
        } else {
            // else handle error
        }
    }
}