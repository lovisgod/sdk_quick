package com.interswitchng.interswitchpossdk.shared.models.transaction

internal data class TransactionStatus (
    val responseMessage: String,
    val responseCode: String,
    val AID: String,
    val telephone: String
)