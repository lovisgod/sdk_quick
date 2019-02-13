package com.igweze.ebi.paxemvcontact.posshim;

import android.content.Context;
import android.util.Log;

import com.igweze.ebi.paxemvcontact.emv.POSDevice;
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
        if (instance == null) instance = new PosInterface(cardService);

        return instance;
    }

    public static void setDalInstance(Context context) {
        try {
            POSDevice.setup(context);
            IDAL idal = POSDevice.getDal();
            Holder.setdal(idal);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Lib " + e);
        }
    }
}
