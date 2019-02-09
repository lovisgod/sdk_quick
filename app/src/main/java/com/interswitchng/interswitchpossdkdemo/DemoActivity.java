package com.interswitchng.interswitchpossdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.Toast;

import com.igweze.ebi.paxemvcontact.POSDeviceService;
import com.igweze.ebi.paxemvcontact.activities.PrintActivity;
import com.igweze.ebi.paxemvcontact.posshim.CardService;
import com.igweze.ebi.paxemvcontact.posshim.PosInterface;
import com.interswitchng.interswitchpossdk.IswPos;


public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        configureTerminal();
//        setupUI();

        Intent intent = new Intent(this, PrintActivity.class);
        startActivity(intent);
    }

    private void configureTerminal() {
        PosInterface.setDalInstance(getApplicationContext());
        CardService cardService = CardService.getInstance(getApplicationContext());
        PosInterface pos = PosInterface.getInstance(cardService);
        POSDeviceService deviceService = new POSDeviceService(pos);

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

}
