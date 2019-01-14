package com.interswitch.posinterface;

public class Transaction {

    public int amount = 0;
    public String cardPin = "";

    public Transaction() {}

    public Transaction(int amount, String pin) {
        this.amount = amount;
        this.cardPin = pin;
    }
}
