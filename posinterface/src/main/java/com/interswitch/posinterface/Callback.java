package com.interswitch.posinterface;

public interface Callback {

    void onCardDetected();
    void onCardRead(String pan);
    void onCardRemoved();
    void onError(PosError error);
}
