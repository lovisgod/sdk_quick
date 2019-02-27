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


import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.shared.errors.NotConfiguredException;
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardTransaction;
import com.interswitchng.smartpos.shared.interfaces.device.IPrinter;
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice;
import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback;
import com.interswitchng.smartpos.shared.models.core.POSConfig;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService;
import com.interswitchng.smartpos.shared.models.core.PurchaseResult;
import com.interswitchng.smartpos.shared.models.core.TerminalInfo;
import com.interswitchng.smartpos.shared.models.core.UserType;
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject;
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus;
import com.interswitchng.smartpos.shared.models.transaction.PaymentType;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardDetail;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse;

import java.util.List;


public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        configureTerminal();
        setupUI();
    }

    private void configureTerminal() {
        POSDevice device;

        if (BuildConfig.MOCK) {
            device = new POSDevice() {
                @Override
                public IPrinter getPrinter() {
                    return new IPrinter() {
                        @Override
                        public PrintStatus printSlip(List<? extends PrintObject> slip, UserType user) {
                            return new PrintStatus.Error("No Printer installed");
                        }

                        @Override
                        public PrintStatus canPrint() {
                            return new PrintStatus.Error("No Printer installed");
                        }
                    };
                }


                @Override
                public EmvCardTransaction getEmvCardTransaction() {
                    return new EmvCardTransaction() {
                        @Override
                        public EmvResult completeTransaction(TransactionResponse response) {
                            return EmvResult.OFFLINE_APPROVED;
                        }

                        @Override
                        public void setEmvCallback(EmvCallback callback) {
                        }

                        @Override
                        public void removeEmvCallback(EmvCallback callback) {
                        }

                        @Override
                        public void setupTransaction(int amount, TerminalInfo terminalInfo) {
                        }

                        @Override
                        public EmvResult startTransaction() {
                            return EmvResult.OFFLINE_APPROVED;
                        }

                        @Override
                        public CardDetail getCardDetail() {
                            return null;
                        }

                        @Override
                        public void cancelTransaction() {
                        }

                        @Override
                        public EmvData getTransactionInfo() {
                            return null;
                        }
                    };
                }
            };
        } else {
            device = POSDeviceService.create(getApplicationContext());
        }

        POSConfig config = new POSConfig("MX5882");
        // configure terminal
        IswPos.setupTerminal(getApplication(), device, config);
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
                    IswPos.getInstance().initiatePayment(this, Integer.valueOf(enteredAmount) * 100, null);
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
            if (data != null) {
                PurchaseResult result = IswPos.getResult(data);
                Log.d("Demo", "" + result);
                toast(result.toString());
            }
        } else {
            // else handle error
        }
    }

    private void toast(String msg) {
        runOnUiThread(() -> {
            Toast.makeText(DemoActivity.this, "Printing Slip", Toast.LENGTH_LONG).show();
        });
    }
}