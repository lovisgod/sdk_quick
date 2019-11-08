package com.interswitchng.interswitchpossdkdemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import com.interswitchng.smartpos.IswPos;
import com.interswitchng.smartpos.emv.pax.services.POSDeviceImpl;
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter;
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader;
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice;
import com.interswitchng.smartpos.shared.models.core.POSConfig;
import com.interswitchng.smartpos.shared.models.core.TerminalInfo;
import com.interswitchng.smartpos.shared.models.core.UserType;
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject;
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData;
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse;
import com.interswitchng.smartpos.usb.UsbConfig;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.Channel;

public class POSApplication extends Application   {

    @Override
    public void onCreate() {
        super.onCreate();

        configureTerminal();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
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
                public EmvCardReader getEmvCardReader() {
                    return new EmvCardReader() {
                        @Override
                        public Object setupTransaction(int amount, @NotNull TerminalInfo terminalInfo, @NotNull Channel<EmvMessage> channel, @NotNull CoroutineScope scope, @NotNull Continuation<? super Unit> o) {
                            return null;
                        }

                        @Override
                        public EmvResult completeTransaction(TransactionResponse response) {
                            return EmvResult.OFFLINE_APPROVED;
                        }

                        @Override
                        public EmvResult startTransaction() {
                            return EmvResult.OFFLINE_APPROVED;
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
            Drawable logo = ContextCompat.getDrawable(this, R.drawable.ic_app_logo);
            Bitmap bm = drawableToBitmap(logo);

            POSDeviceImpl service = POSDeviceImpl.create(getApplicationContext());
            service.setCompanyLogo(bm);
            device = service;
        }

        String clientId = "IKIA4733CE041F41ED78E52BD3B157F3AAE8E3FE153D";
        String clientSecret = "t1ll73stS3cr3t";
        String alias = "000001";
        String merchantCode = "MX1065";

//        if (BuildConfig.DEBUG && BuildConfig.MOCK) {
//            alias = "000007";
//            clientId = "IKIAB23A4E2756605C1ABC33CE3C287E27267F660D61";
//            clientSecret = "secret";
//            merchantCode = "MX5882";
//        }

        String merchantPhone = "080311402392";
        POSConfig config = new POSConfig(alias, clientId, clientSecret, merchantCode, merchantPhone);
//        config.with(new UsbConfig());

        // setup terminal
        IswPos.setupTerminal(this, device, config);
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
}
