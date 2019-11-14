package com.interswitchng.interswitchpossdkdemo;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceImpl;
import com.interswitchng.smartpos.shared.errors.NotConfiguredException;
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice;
import com.interswitchng.smartpos.shared.models.core.Environment;
import com.interswitchng.smartpos.shared.models.core.POSConfig;
import com.interswitchng.smartpos.shared.models.core.PurchaseResult;
import com.interswitchng.smartpos.shared.models.transaction.PaymentType;
import com.interswitchng.smartpos.usb.UsbConfig;
import com.interswitchng.smartpos.usb.interfaces.MessageListener;

import java.text.NumberFormat;


public class DemoActivity extends AppCompatActivity implements Keyboard.KeyBoardListener, MessageListener {
    private static String KEY_ENABLE_USB = "key_enable_usb";
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
//
//        if (BuildConfig.MOCK) {
//            device = new POSDevice() {
//                @Override
//                public DevicePrinter getPrinter() {
//                    return new DevicePrinter() {
//                        @Override
//                        public PrintStatus printSlip(List<? extends PrintObject> slip, UserType user) {
//                            return new PrintStatus.Error("No DevicePrinterImpl installed");
//                        }
//
//                        @Override
//                        public PrintStatus canPrint() {
//                            return new PrintStatus.Error("No DevicePrinterImpl installed");
//                        }
//                    };
//                }
//
//
//                @Override
//                public EmvCardReader getEmvCardReader() {
//                    return new EmvCardReader() {
//
//
//                        @Override
//                        public Object setupTransaction(int amount, TerminalInfo terminalInfo,  kotlinx.coroutines.channels.Channel<EmvMessage> channel, kotlinx.coroutines.CoroutineScope scope,  kotlin.coroutines.Continuation<? super kotlin.Unit> o) {
//                            return null;
//                        }
//
//                        @Override
//                        public EmvResult completeTransaction(TransactionResponse response) {
//                            return EmvResult.OFFLINE_APPROVED;
//                        }
//
//                        @Override
//                        public EmvResult startTransaction() {
//                            return EmvResult.OFFLINE_APPROVED;
//                        }
//
//
//                        @Override
//                        public void cancelTransaction() {
//                        }
//
//                        @Override
//                        public EmvData getTransactionInfo() {
//                            return null;
//                        }
//                    };
//                }
//            };
//        } else {


            Drawable logo = ContextCompat.getDrawable(this, R.drawable.ic_app_logo);
            Bitmap bm = drawableToBitmap(logo);

            POSDeviceImpl service = POSDeviceImpl.create(getApplicationContext());
            service.setCompanyLogo(bm);
            device = service;


//        }

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

        boolean enableUsb = getIntent().getBooleanExtra(KEY_ENABLE_USB, false);
        POSConfig config = new POSConfig(alias, clientId, clientSecret, merchantCode,"", Environment.Test );

        if (enableUsb) {
            UsbConfig usbConfig = new UsbConfig();
            usbConfig.setMessageListener(this);
            config.with(usbConfig);
        }

        // setup terminal
        IswPos.setupTerminal(getApplication(), device, config,false);
    }

    private void setupUI() {
        amount = findViewById(R.id.amount);
        amount.setText("0.00");
        keyboard = new Keyboard(this, this);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
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
        } else if (item.getItemId() == R.id.enable_usb) {
            restartApplication();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DemoActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void restartApplication() {
        Intent intent = getIntent();
        intent.putExtra(KEY_ENABLE_USB, true);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    public void onTextChange(String s) {
        if (s != null && !s.equals(current)) {

            String text = s.isEmpty() ? defaultAmount : s;

            String cleanString = text.replaceAll("[$,.]", "");

            double parsed = Double.parseDouble(cleanString);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(2);
            numberFormat.setMaximumFractionDigits(2);

            String formatted = numberFormat.format((parsed / 100));

            currentAmount = Integer.valueOf(cleanString);
            amount.setText(formatted);
            current = cleanString;
            keyboard.setText(cleanString);
        }
    }

    @Override
    public void onSubmit(String text) {

        String enteredAmount = amount.getText().toString();
        if (enteredAmount.isEmpty() || enteredAmount.equals(defaultAmount)) {
            toast("Amount value is required");
        } else {
            makePayment(currentAmount, null);
        }
    }

    private void makePayment(int amount, PaymentType type) {
        try {
            // trigger payment
            IswPos.getInstance().initiatePayment(this, amount, type);
        } catch (NotConfiguredException e) {
            toast("Pos has not been configured");
            Log.d("DEMO", e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(PaymentType paymentType, int amount) {
        makePayment(amount, paymentType);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // handle success
            if (data != null) {
                PurchaseResult result = IswPos.getResult(data);
                Log.d("Demo", "" + result);
                toast(result.toString());
                // reset the amount back to default
                if (result.getResponseCode().equals("00"))
                    onTextChange(defaultAmount);
            }
        } else {
            // else handle error
        }
    }

}
