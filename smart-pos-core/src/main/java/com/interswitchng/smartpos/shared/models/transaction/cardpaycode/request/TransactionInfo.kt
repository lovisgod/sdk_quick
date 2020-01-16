package com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request

import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult


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
    var iccFull: IccData,
    var src: String, // service restriction code
    var csn: String, // card sequence number
    val amount: Int,
    val stan: String,
    val purchaseType: PurchaseType,
    val accountType: AccountType,
    var originalTransactionInfoData: OriginalTransactionInfoData? = null,
    val pinKsn: String) {


    companion object {
        fun fromEmv(emv: EmvData, paymentInfo: PaymentInfo, purchaseType: PurchaseType, accountType: AccountType) = TransactionInfo (
            cardExpiry =  emv.cardExpiry,
            cardPAN = emv.cardPAN,
            cardPIN =  emv.cardPIN,
            iccFull=emv.iccFullData,
            cardTrack2 =  emv.cardTrack2,
            icc = emv.icc,
            src = emv.src,
            csn = emv.csn,
            pinKsn=emv.pinKsn,
            amount = paymentInfo.amount * 100,
            stan = paymentInfo.getStan(),
            purchaseType = purchaseType,
            originalTransactionInfoData=OriginalTransactionInfoData(paymentInfo.originalStanId,"",paymentInfo.originalAuthId,""),
            accountType = accountType)

        fun fromTxnResult(txnResult: TransactionResult) = TransactionInfo(
            cardExpiry = txnResult.cardExpiry,
            cardPAN = txnResult.cardPan,
            cardPIN = txnResult.cardPin,
            cardTrack2 =  txnResult.cardTrack2,
            icc = txnResult.icc,
            src = txnResult.src,
            csn = txnResult.csn,
            amount = txnResult.amount.toInt(),
            stan = txnResult.stan,
            purchaseType = PurchaseType.Card,
            accountType = AccountType.Default,
            originalTransactionInfoData = OriginalTransactionInfoData(
                originalTransmissionDateAndTime = txnResult.originalTransmissionDateTime,
                month = txnResult.month, time = txnResult.time),
            pinKsn = "",
            iccFull = IccData()
        )

    }
}

internal data class OriginalTransactionInfoData(
    var originalStan: String? = EMPTY_STRING,
    var originalTransmissionDateAndTime: String = EMPTY_STRING,
    var originalAuthorizationId: String? = EMPTY_STRING,
    var originalAmount: String = EMPTY_STRING,
    var month: String = EMPTY_STRING,
    var time: String = EMPTY_STRING
)