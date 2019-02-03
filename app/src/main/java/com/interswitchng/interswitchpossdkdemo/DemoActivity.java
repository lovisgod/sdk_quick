package com.interswitchng.interswitchpossdkdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.interswitchng.interswitchpossdk.IswPos;
import com.interswitchng.interswitchpossdk.shared.models.POSConfiguration;


public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        configureTerminal();
        setupUI();
    }

    private void configureTerminal() {
        String alias = "000007";
        String terminalId = "2069018M";
        String merchantId = "IBP000000001384";
        String merchantLocation = "AIRTEL NETWORKS LIMITED PH MALL";
        // String currencyCode = "566";
        // String posGeoCode = "0023400000000056";
        String terminalType = "PAX";
        String uniqueId = "280-820-589";
        String merchantCode = "MX5882";

        POSConfiguration config = new POSConfiguration(alias, terminalId, merchantId, terminalType, uniqueId, merchantLocation, merchantCode);

        IswPos.configureTerminal(getApplication(), config);
    }

    private void setupUI() {
        final EditText amount = findViewById(R.id.amount);
        final AppCompatButton button = findViewById(R.id.btnSubmitAmount);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String enteredAmount = amount.getText().toString();
                if (enteredAmount.isEmpty()) {
                    Toast.makeText(DemoActivity.this, "Amount value is required", Toast.LENGTH_LONG).show();
                } else {
                   // trigger payment
                   IswPos.getInstance().initiatePayment(Integer.valueOf(enteredAmount));
                }
            }
        });

    }
}
