package com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request

internal data class TransactionInfo(
        val cardExpiry: String,
        val cardPIN: String,
        val cardPAN: String,
        val cardTrack2: String,
        val icc: String,
        val amount: Int,
        val purchaseType: PurchaseType,
        val accountType: AccountType) {


    companion object {
        fun fromEmv(emv: EmvData, amount: Int, purchaseType: PurchaseType, accountType: AccountType) = TransactionInfo (
                cardExpiry =  emv.cardExpiry,
                cardPAN = emv.cardPAN,
                cardPIN =  emv.cardPIN,
                cardTrack2 =  emv.cardTrack2,
                icc = emv.icc,
                amount = amount,
                purchaseType = purchaseType,
                accountType = accountType)
    }
}