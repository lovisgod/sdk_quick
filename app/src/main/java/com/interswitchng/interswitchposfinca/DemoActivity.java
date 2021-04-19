package com.interswitchng.interswitchposfinca;


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

import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.shared.errors.NotConfiguredException;
import com.interswitchng.smartpos.shared.models.core.PurchaseResult;
import com.interswitchng.smartpos.shared.models.transaction.PaymentType;
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

        setupUI();
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
            // show settings for terminal configuration
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
        // trigger payment
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
