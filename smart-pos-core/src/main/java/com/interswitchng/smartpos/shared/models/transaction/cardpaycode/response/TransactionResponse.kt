package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response


/**
 * This class captures the transaction response from EPMS
 * for a given purchase request
 */
data class TransactionResponse(
        val responseCode: String, // response code
        val authCode: String, // authorization code
        val stan: String,
        val scripts: String,
        val transmissionDateTime: String = ""
)