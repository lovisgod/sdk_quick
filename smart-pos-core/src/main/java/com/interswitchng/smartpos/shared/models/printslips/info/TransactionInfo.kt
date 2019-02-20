package com.interswitchng.smartpos.shared.models.printslips.info

import com.interswitchng.smartpos.shared.models.transaction.PaymentType

internal data class TransactionInfo(
        val paymentType: PaymentType,
        val stan: String,
        val dateTime: String,
        val amount: String,
        val type: TransactionType,
        val cardPan: String,
        val cardType: String,
        val cardExpiry: String,
        val authorizationCode: String,
        val pinStatus: String
)