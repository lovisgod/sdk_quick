package com.interswitch.posinterface.posshim;

import android.content.Context;
import android.util.Log;

import com.interswitch.posinterface.Holder;
import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;

public final class PosInterface {

    private static PosInterface instance;

    private CardService mCardDetectionService;

    private PosInterface(CardService cardService) {
        mCardDetectionService = cardService;
//        mCardDetectionService.startListening();
    }

    public void attachCallback(CardService.Callback callback) {
        mCardDetectionService.setCallback(callback);
    }

    public void removeCallback(CardService.Callback callback) {
        mCardDetectionService.removeCallback(callback);
    }

    public void setTransaction(Transaction transaction) {
        // mCardDetectionService.setTransaction(transaction);
    }

    public void checkPin(String pin) {
        mCardDetectionService.checkPin(pin);
    }

    public void print(PrintConfiguration data) {
        mCardDetectionService.print(data);
    }

    public static synchronized PosInterface getInstance(CardService cardService) {
        if (!hasLoadedLibraries) loadLibraries();
        if (instance == null) instance = new PosInterface(cardService);

        return instance;
    }

    public static void setDalInstanced(Context context) {
        try {
            IDAL idal = NeptuneLiteUser.getInstance().getDal(context);
            Holder.setdal(idal);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Lib " + e);
        }
    }

    private static boolean hasLoadedLibraries = false;

    private static void loadLibraries() {
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

        hasLoadedLibraries = true;
    }
}
