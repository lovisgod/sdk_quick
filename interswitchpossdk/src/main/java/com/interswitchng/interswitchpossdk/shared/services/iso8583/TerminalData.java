package com.interswitchng.interswitchpossdk.shared.services.iso8583;

import android.content.SharedPreferences;

import com.google.gson.Gson;

public class TerminalData {

    public String merchantId = "";
    public String merchantLocation = "";
    public String merchantCategoryCode = "";
    public String countryCode = "";
    public String currencyCode = "";
    public int callHomeTimeInMin = 0;
    public int serverTimeoutInSec = 0;

    public static final String PERSIST_KEY = "terminal_data";

    @Override
    public String toString() {

        return String.format("MerchantId => %s\n" +
                "MerchantLocation => %s\n" +
                "MerchantCategoryCode => %s\n" +
                "CountryCode => %s\n" +
                "CurrencyCode => %s\n", merchantId, merchantLocation, merchantCategoryCode, countryCode, currencyCode);
    }

    public void persist(SharedPreferences preferences) {

        String JSON = new Gson().toJson(this);
        preferences.edit().putString(PERSIST_KEY, JSON).apply();
    }

    public static TerminalData get(SharedPreferences preferences) {

        String JSON = preferences.getString(PERSIST_KEY, "");
        return new Gson().fromJson(JSON, TerminalData.class);
    }
}
