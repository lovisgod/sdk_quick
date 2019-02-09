package com.igweze.ebi.paxemvcontact.posshim;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.igweze.ebi.paxemvcontact.emv.DeviceImplNeptune;
import com.igweze.ebi.paxemvcontact.emv.EmvImplementation;
import com.igweze.ebi.paxemvcontact.emv.PinCallback;
import com.igweze.ebi.paxemvcontact.utilities.EmvUtils;
import com.pax.dal.IDAL;
import com.pax.dal.IIcc;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.dal.exceptions.IccDevException;
import com.pax.jemv.device.DeviceManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CardService implements PinCallback {

    public static final String TAG = "CardEx";

    private static final int STATE_INSERTED = 1;
    private static final int STATE_REMOVED = 2;
    private static final int LOOKUP_INTERVAL = 3 * 1000;

    private int currentState = STATE_REMOVED;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Handler handler = new Handler();
    private Handler mHandler = new Handler();
    private IIcc icc;
    private Context mContext;
    private Callback callback;
    private Transaction mTransaction;

    private static CardService sInstance;
    private static final Object LOCK = new Object();
    private EmvImplementation emv;

    public interface Callback {

        void onCardDetected();
        void onCardRead(Card card);
        void onCardRemoved();
        void onError(PosError error);
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void removeCallback(Callback callback) {
        this.callback = null;
    }

    public void checkPin(String pin) {
        mTransaction = new Transaction(0, pin);

        executorService.execute(() -> {
            //startReading();
        });
    }

    public static synchronized CardService getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CardService(context);
        }

        return sInstance;
    }

    private CardService(Context context) {
        IDAL dal = Holder.getIdal();
        icc = dal.getIcc();
        mContext = context.getApplicationContext();
        DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance());
        Printer.getInstance().init();
    }

    private void setCurrentState(int state) {

        if (state == STATE_REMOVED) {
            schedule();
        }

        currentState = state;
    }

    public void startListening() {

        if (currentState == STATE_INSERTED) {
            return;
        }

        setCurrentState(STATE_REMOVED);
    }

    private void schedule() {
        handler.postDelayed(runnable, LOOKUP_INTERVAL);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    detectCardInserted();
                }
            });
        }
    };

    private void detectCardInserted() {

        byte iccSlot =  (byte)0;

        while (true) {
            try {
                if (icc.detect(iccSlot)) {

                    if (callback != null) {
                        callback.onCardDetected();

                        if (mTransaction != null) {
                            startReading();
                        }
                    }

                    startListeningForRemovedEvent();
                    setCurrentState(STATE_INSERTED);
                    break;
                }
            } catch (IccDevException e) {
            }
        }
    }

    private void startListeningForRemovedEvent() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (emv == null) {

                    emv = new EmvImplementation(CardService.this);
                }

                int ret = -10;
                synchronized (LOCK) {
                    ret = emv.startContactEmvTransaction();
                }

                if (ret == -1) {
                    setCurrentState(STATE_REMOVED);

                    if (callback != null) {
                        callback.onCardRemoved();
                    }
                    mHandler.removeCallbacks(this);
                }else {
                    mHandler.postDelayed(this, 5000);
                }
            }
        };

        mHandler.postDelayed(runnable, 5000);
    }

    private void startReading() {

        if (emv == null) {
            emv = new EmvImplementation(this);
        }

        int ret = -10;

        synchronized (LOCK) {
            ret = emv.startContactEmvTransaction();
        }

        if (ret == -1) {
            mHandler.removeCallbacksAndMessages(null);

            setCurrentState(STATE_REMOVED);
            return;
        }

        if (ret == TransResult.EMV_ARQC) {
            synchronized (LOCK) {
                ret = emv.completeContactEmvTransaction();
            }
        }

        if (ret == TransResult.EMV_ONLINE_APPROVED || ret == TransResult.EMV_OFFLINE_APPROVED
                || ret == TransResult.EMV_ONLINE_CARD_DENIED) {

            byte[] track2 = emv.getTlv(0x57);
            String strTrack2 = EmvUtils.bcd2Str(track2);

            strTrack2 = strTrack2.split("F")[0];
            final String pan = strTrack2.split("D")[0];
            final String expiry = strTrack2.split("D")[1].substring(0, 4);

            Card card = new Card();
            card.pan = pan;
            card.expiry = expiry;

            callback.onCardRead(card);
        }

    }

    private void setNormalFont(Printer printer) {

    }

    private void setLargeFont(Printer printer) {
        printer.fontSet(EFontTypeAscii.FONT_16_32,
                EFontTypeExtCode.FONT_16_32);

    }

    public void print(final PrintConfiguration data) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Printer printer = Printer.getInstance();

                printer.init();

                setNormalFont(printer);

                printer.step(100);

                List<Object> printObjects = data.getPrintObjects();
                for (Object object: printObjects) {

                    if (object instanceof String) {
                        printer.printStr((String) object + "\n\n", null);
                    }else if (object instanceof PrintConfiguration.Line) {
                        printer.getDotLine();
                    }else if (object instanceof Bitmap) {
                        printer.printBitmap((Bitmap) object);
                    }
                }
                printer.step(50);
                Log.d("Printer", "print slip size: "+ printObjects.size());

                String status = printer.start();
                log("Printer => " + status);
            }
        });
    }


    @Override
    public void showInputCard() {

    }

    @Override
    public int getPinResult(@NotNull String panBlock) {
        return 0;
    }

    @Override
    public void enterPin(boolean isOnline, int offlineTriesLeft, @NotNull String panBlock) {

    }

    void log(String m) {
        Log.d(TAG, m);
    }
}
