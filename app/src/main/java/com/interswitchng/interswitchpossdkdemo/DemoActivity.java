package com.interswitchng.interswitchpossdkdemo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceImpl;
import com.interswitchng.smartpos.shared.errors.NotConfiguredException;
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader;
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter;
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


public class DemoActivity extends AppCompatActivity implements Keyboard.KeyBoardListener {

    final private String defaultAmount = "0.00";
    private String current = "";
    private TextView amount;
    private int currentAmount;
    private Keyboard keyboard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        setSupportActionBar(findViewById(R.id.homeToolbar));

        configureTerminal();
        setupUI();
    }

    private void configureTerminal() {
        POSDevice device;

        if (BuildConfig.MOCK) {
            device = new POSDevice() {
                @Override
                public DevicePrinter getPrinter() {
                    return new DevicePrinter() {
                        @Override
                        public PrintStatus printSlip(List<? extends PrintObject> slip, UserType user) {
                            return new PrintStatus.Error("No DevicePrinterImpl installed");
                        }

                        @Override
                        public PrintStatus canPrint() {
                            return new PrintStatus.Error("No DevicePrinterImpl installed");
                        }
                    };
                }


                @Override
                public EmvCardReader getEmvCardTransaction() {
                    return new EmvCardReader() {
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

            POSDeviceImpl service  = POSDeviceImpl.create(getApplicationContext());
            service.setCompanyLogo(bm);
            device = service;
        }

        String clientId = "IKIA4733CE041F41ED78E52BD3B157F3AAE8E3FE153D";
        String clientSecret = "t1ll73stS3cr3t";
        String alias = "000001";
        String merchantCode = "MX1065";

        if (BuildConfig.DEBUG && BuildConfig.MOCK) {
            alias = "000007";
            clientId = "IKIAB23A4E2756605C1ABC33CE3C287E27267F660D61";
            clientSecret = "secret";
            merchantCode = "MX5882";
        }

        POSConfig config = new POSConfig(alias, clientId, clientSecret, merchantCode);

        // setup terminal
        IswPos.setupTerminal(getApplication(), device, config);
    }

    private void setupUI() {
        amount = findViewById(R.id.amount);
        amount.setText("0.00");
        keyboard = new Keyboard(this, this);
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

    private void toast(String msg) {
        runOnUiThread(() -> Toast.makeText(DemoActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    @Override
    public void onTextChange(String s) {
        if (s != null && !s.isEmpty() && !s.equals(current)) {

            String cleanString = s.replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);

            String formatted = numberFormat.format((parsed / 100));

            current = formatted;
            currentAmount = Integer.valueOf(cleanString);
            amount.setText(formatted);
            keyboard.setText(formatted);
        }
    }

    @Override
    public void onSubmit(String text) {

        String enteredAmount = amount.getText().toString();
        if (enteredAmount.isEmpty() || enteredAmount.equals(defaultAmount)) {
            toast( "Amount value is required");
        } else {
            try {
                // trigger payment
                IswPos.getInstance().initiatePayment(this, currentAmount, null);
            } catch (NotConfiguredException e) {
                toast("Pos has not been configured");
                Log.d("DEMO", e.getMessage());
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // handle success
            if (data != null) {
                PurchaseResult result = IswPos.getResult(data);
                Log.d("Demo", "" + result);
                toast(result.toString());
                onTextChange(defaultAmount);
            }
        } else {
            // else handle error
        }
    }

}
