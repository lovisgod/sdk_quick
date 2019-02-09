package com.igweze.ebi.paxemvcontact.iso8583;

public final class Card {

    public String pan = "";
    public String expiry = "";
    public String track2Data = "";
    public String pin = "";

    @Override
    public String toString() {
        return pan + "\n" + expiry + "\n" + track2Data + "\n" + pin;
    }
}
