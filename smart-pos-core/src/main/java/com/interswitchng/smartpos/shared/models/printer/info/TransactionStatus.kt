package com.interswitchng.smartpos.shared.models.printer.info

internal data class TransactionStatus (
    val responseMessage: String,
    val responseCode: String,
    val AID: String,
    val telephone: String
)