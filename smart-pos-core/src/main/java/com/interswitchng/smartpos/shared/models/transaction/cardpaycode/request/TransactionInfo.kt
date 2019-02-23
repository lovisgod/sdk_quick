package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo

internal data class TransactionInfo(
        val cardExpiry: String,
        val cardPIN: String,
        val cardPAN: String,
        val cardTrack2: String,
        val icc: String,
        val src: String, // service restriction code
        val amount: Int,
        val stan: String,
        val purchaseType: PurchaseType,
        val accountType: AccountType) {


    companion object {
        fun fromEmv(emv: EmvData, paymentInfo: PaymentInfo, purchaseType: PurchaseType, accountType: AccountType) = TransactionInfo (
                cardExpiry =  emv.cardExpiry,
                cardPAN = emv.cardPAN,
                cardPIN =  emv.cardPIN,
                cardTrack2 =  emv.cardTrack2,
                icc = emv.icc,
                src = emv.src,
                amount = paymentInfo.amount,
                stan = paymentInfo.stan,
                purchaseType = purchaseType,
                accountType = accountType)
    }
}