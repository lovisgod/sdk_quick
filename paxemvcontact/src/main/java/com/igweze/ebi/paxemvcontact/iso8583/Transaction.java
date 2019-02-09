package com.igweze.ebi.paxemvcontact.iso8583;

public class Transaction {

    public int amount = 0;
    public String cardPin = "";
    public Card card;

    public Transaction() {}

    public Transaction(int amount, String pin) {
        this.amount = amount;
        this.cardPin = pin;
    }
}
