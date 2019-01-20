package com.interswitch.posinterface.posshim;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;

import com.interswitch.posinterface.DeviceImplNeptune;
import com.interswitch.posinterface.Holder;
import com.interswitch.posinterface.ImplEmv;
import com.interswitch.posinterface.TransResult;
import com.pax.dal.IDAL;
import com.pax.dal.IIcc;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.pax.dal.exceptions.IccDevException;
import com.pax.jemv.device.DeviceManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CardService {

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
    private ImplEmv emv;


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

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                startReading();
            }
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

                    emv = new ImplEmv(mContext, mTransaction);
                }

                int ret = -10;
                synchronized (LOCK) {
                    ret = emv.startContactEmvTrans();
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
            emv = new ImplEmv(mContext, mTransaction);
        }

        int ret = -10;

        synchronized (LOCK) {
            ret = emv.startContactEmvTrans();
        }

        if (ret == -1) {
            mHandler.removeCallbacksAndMessages(null);

            setCurrentState(STATE_REMOVED);
            return;
        }

        if (ret == TransResult.EMV_ARQC) {
            synchronized (LOCK) {
                ret = emv.CompleteContactEmvTrans();
            }
        }

        if (ret == TransResult.EMV_ONLINE_APPROVED || ret == TransResult.EMV_OFFLINE_APPROVED
                || ret == TransResult.EMV_ONLINE_CARD_DENIED) {

            byte[] track2 = ImplEmv.getTlv(0x57);
            String strTrack2 = Holder.getConvert().bcdToStr(track2);

            strTrack2 = strTrack2.split("F")[0];
            final String pan = strTrack2.split("D")[0];
            final String expiry = strTrack2.split("D")[1].substring(0, 4);

            Card card = new Card();
            card.pan = pan;
            card.expiry = expiry;

            callback.onCardRead(card);
        }

    }

    public void print(final PrintConfiguration data) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                Printer printer = Printer.getInstance();
                printer.fontSet(EFontTypeAscii.FONT_16_32,
                        EFontTypeExtCode.FONT_16_32);

                for (Object object: data.getPrintObjects()) {

                    if (object instanceof String) {
                        printer.printStr((String) object, null);
                    }else if (object instanceof PrintConfiguration.Line) {
                        printer.getDotLine();
                    }else if (object instanceof Bitmap) {
                        printer.printBitmap((Bitmap) object);
                    }
                }

                String status = Printer.getInstance().start();
                log("Printer => " + status);
            }
        });
    }

    void log(String m) {
        Log.d(TAG, m);
    }
}
