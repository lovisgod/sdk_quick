package com.interswitchng.interswitchpossdk.shared.models.request

internal data class TransactionType(
        val currencyCode: String,
        val merchantId: String,
        val merchantLocation: String,
        val posGeoCode: String,
        val terminalType: String,
        val uniqueId: String
)