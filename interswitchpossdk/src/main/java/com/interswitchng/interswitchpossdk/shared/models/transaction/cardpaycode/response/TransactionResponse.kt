package com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.response

internal data class TransactionResponse(
        val code: String,
        val stan: String,
        val scripts: String
)