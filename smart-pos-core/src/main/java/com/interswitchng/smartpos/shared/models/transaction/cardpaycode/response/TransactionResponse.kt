package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response

import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType


/**
 * This class captures the transaction response from EPMS
 * for a given purchase request
 */
data class TransactionResponse(
        val responseCode: String, // response code
        val authCode: String, // authorization code
        val stan: String,
        val scripts: String = EMPTY_STRING,
        val transmissionDateTime: String = EMPTY_STRING,
        val month: String = EMPTY_STRING,
        val time: String = EMPTY_STRING,
        val responseDescription: String? = null,
        val name: String = EMPTY_STRING,
        val ref: String = EMPTY_STRING,
        val rrn: String = EMPTY_STRING,
        val type: TransactionType = TransactionType.CashOutInquiry
)