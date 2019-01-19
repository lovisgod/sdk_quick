package com.interswitch.posinterface.posshim;

import android.content.Context;
import android.util.Log;

import com.interswitch.posinterface.Holder;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

public final class PosInterface {

    private static PosInterface instance;

    static {

        Log.d("CardEx", "Loading Libs");
        System.loadLibrary("F_DEVICE_LIB_PayDroid");
        System.loadLibrary("F_PUBLIC_LIB_PayDroid");
        System.loadLibrary("F_EMV_LIB_PayDroid");
        System.loadLibrary("F_ENTRY_LIB_PayDroid");
        System.loadLibrary("F_MC_LIB_PayDroid");
        System.loadLibrary("F_WAVE_LIB_PayDroid");
        System.loadLibrary("F_AE_LIB_PayDroid");
        System.loadLibrary("F_QPBOC_LIB_PayDroid");
        System.loadLibrary("F_DPAS_LIB_PayDroid");
        System.loadLibrary("F_JCB_LIB_PayDroid");
        System.loadLibrary("F_PURE_LIB_PayDroid");
        System.loadLibrary("JNI_EMV_v100");
        System.loadLibrary("JNI_ENTRY_v100");
        System.loadLibrary("JNI_MC_v100");
        System.loadLibrary("JNI_WAVE_v100");
        System.loadLibrary("JNI_AE_v100");
        System.loadLibrary("JNI_QPBOC_v100");
        System.loadLibrary("JNI_DPAS_v100");
        System.loadLibrary("JNI_JCB_v100");
        System.loadLibrary("JNI_PURE_v100");
    }

    private CardService mCardDetectionService;

    private PosInterface(Context context, CardService cardService) {

        try {
            IDAL idal = NeptuneLiteUser.getInstance().getDal(context);
            Holder.setdal(idal);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Lib " + e);
        }


        mCardDetectionService = cardService;
        mCardDetectionService.startListening();
    }

    public static synchronized PosInterface getInstance(Context context, CardService cardService) {

        if (instance == null) instance = new PosInterface(context, cardService);

        return instance;
    }

    public void attachCallback(CardService.Callback callback) {
        mCardDetectionService.setCallback(callback);
    }

    public void setTransaction(Transaction transaction) {
        // mCardDetectionService.setTransaction(transaction);
    }

    public void print(PrintConfiguration data) {
        mCardDetectionService.print(data);
    }
}
