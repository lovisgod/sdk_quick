package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response

data class TransactionResponse(
        val responseCode: String, // response code
        val authCode: String, // authorization code
        val stan: String,
        val scripts: String
)