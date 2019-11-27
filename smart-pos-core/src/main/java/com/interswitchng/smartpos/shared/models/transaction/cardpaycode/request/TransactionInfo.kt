package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo


/**
 * This class captures transaction request information to be issued
 * out to the EPMS for [PurchaseType] Transactions
 */
internal data class TransactionInfo(
        val cardExpiry: String,
        val cardPIN: String,
        val cardPAN: String,
        val cardTrack2: String,
        var icc: String,
        var src: String, // service restriction code
        var csn: String, // card sequence number
        val amount: Int,
        val stan: String,
        val purchaseType: PurchaseType,
        val accountType: AccountType,
        var originalTransactionInfoData: OriginalTransactionInfoData? = null) {


    companion object {
        fun fromEmv(emv: EmvData, paymentInfo: PaymentInfo, purchaseType: PurchaseType, accountType: AccountType) = TransactionInfo (
                cardExpiry =  emv.cardExpiry,
                cardPAN = emv.cardPAN,
                cardPIN =  emv.cardPIN,
                cardTrack2 =  emv.cardTrack2,
                icc = emv.icc,
                src = emv.src,
                csn = emv.csn,
                amount = paymentInfo.amount * 100,
                stan = paymentInfo.getStan(),
                purchaseType = purchaseType,
                accountType = accountType)

    }
}

internal data class OriginalTransactionInfoData(
        var originalStan: String?,
        var originalTransmissionDateAndTime: String?,
        var originalAuthorizationId: String?,
        var originalAmount: String?
) {
        companion object {
                fun addOriginalTransactionInfo(originalStan: String? = null,
                                               originalTransmissionDateAndTime: String? = null,
                                               originalAuthorizationId: String? = null,
                                               originalAmount: String? = null) = OriginalTransactionInfoData(originalStan,
                        originalTransmissionDateAndTime,
                        originalAuthorizationId,
                                originalAmount)
        }
}