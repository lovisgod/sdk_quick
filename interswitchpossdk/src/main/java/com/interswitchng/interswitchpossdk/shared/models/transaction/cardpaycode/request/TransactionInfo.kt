package com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request

internal data class TransactionInfo(
        val cardExpiry: String,
        val cardPIN: String,
        val cardPAN: String,
        val cardTrack2: String,
        val icc: String,
        val amount: Int,
        val purchaseType: PurchaseType,
        val accountType: AccountType
)