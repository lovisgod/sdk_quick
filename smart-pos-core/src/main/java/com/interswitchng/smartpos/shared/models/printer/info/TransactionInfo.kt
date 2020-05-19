package com.interswitchng.smartpos.shared.models.printer.info

import com.interswitchng.smartpos.shared.models.transaction.PaymentType

/**
 * This class captures information to be
 * printed out for a purchase transaction
 */
data class TransactionInfo(
        val paymentType: PaymentType,
        val stan: String,
        val dateTime: String,
        val amount: String,
        val type: TransactionType,
        val cardPan: String,
        val cardType: String,
        val cardExpiry: String,
        val authorizationCode: String,
        val pinStatus: String,
        val originalDateTime: String,
        val responseCode: String,
        val rrn: String
)