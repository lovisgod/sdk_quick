package com.interswitchng.interswitchpossdkdemo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService;
import com.interswitchng.smartpos.shared.errors.NotConfiguredException;
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardTransaction;
import com.interswitchng.smartpos.shared.interfaces.device.IPrinter;
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice;
import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback;
import com.interswitchng.smartpos.shared.models.core.POSConfig;
import com.interswitchng.smartpos.shared.models.core.PurchaseResult;
import com.interswitchng.smartpos.shared.models.core.TerminalInfo;
import com.interswitchng.smartpos.shared.models.core.UserType;
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject;
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardDetail;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse;

import java.text.NumberFormat;
import java.util.List;


public class DemoActivity extends AppCompatActivity implements TextWatcher {

    private String current = "";
    private EditText amount;
    private int currentAmount;

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
            Resources resources = getResources();
            Drawable logo = ContextCompat.getDrawable(this, R.drawable.ic_app_logo);
            Bitmap bm = drawableToBitmap(logo);

            POSDeviceService service  = POSDeviceService.create(getApplicationContext());
            service.setCompanyLogo(bm);
            device = service;
        }

        String clientId = "IKIA4733CE041F41ED78E52BD3B157F3AAE8E3FE153D";
        String clientSecret = "t1ll73stS3cr3t";
        String alias = "000001";
        String merchantCode = "MX1065";

        POSConfig config = new POSConfig(alias, clientId, clientSecret, merchantCode);

        // configure terminal
        IswPos.setupTerminal(getApplication(), device, config);
    }

    private void setupUI() {
        amount = findViewById(R.id.amount);
        final AppCompatButton button = findViewById(R.id.btnSubmitAmount);

        amount.addTextChangedListener(this);

        button.setOnClickListener(v -> {
            String enteredAmount = amount.getText().toString();
            if (enteredAmount.isEmpty()) {
                Toast.makeText(DemoActivity.this, "Amount value is required", Toast.LENGTH_LONG).show();
            } else {
                try {
                    // trigger payment
                    IswPos.getInstance().initiatePayment(this, currentAmount, null);
                } catch (NotConfiguredException e) {
                    toast("Pos has not been configured");
                    Log.d("DEMO", e.getMessage());
                }
            }
        });


    }


    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
            Toast.makeText(DemoActivity.this, msg, Toast.LENGTH_LONG).show();
        });
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s != null && !s.toString().equals(current)) {
            amount.removeTextChangedListener(this);

            String cleanString = s.toString().replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);

            String formatted = numberFormat.format((parsed / 100));

            current = formatted;
            currentAmount = Integer.valueOf(cleanString);
            amount.setText(formatted);
            amount.setSelection(formatted.length());

            amount.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
