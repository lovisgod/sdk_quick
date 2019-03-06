package com.interswitchng.smartpos.emv.pax.models


internal data class TerminalConfig(
        val supportpse: Boolean,
        val floorlimitcheck: Boolean,
        val floorlimit: Int,
        val tacdenial: String,
        val taconline: String,
        val tacdefault: String,
        val ddol: String,
        val tdol: String,
        val version: String,
        val riskdata: String,
        val terminalcountrycode: String,
        val terminaltype: String,
        val terminalcapability: String,
        val extendedterminalcapability: String,
        val referercurrencycode: String,
        val merchantcatcode: String
)