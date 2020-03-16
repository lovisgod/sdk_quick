package com.interswitchng.smartpos.shared.models.transaction

import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * This class is responsible for capturing
 * transaction results, to be logged to DB
 */
@Parcelize
open class TransactionLog(
        @PrimaryKey var id: Int = 0,
        var paymentType: Int = 0,
        var stan: String = "",
        var dateTime: String = "",
        var amount: String = "",
        var transactionType: Int = TransactionType.Purchase.ordinal,
        var cardPan: String = "",
        var cardType: Int = CardType.None.ordinal,
        var cardExpiry: String = "",
        var authorizationCode: String = "",
        var pinStatus: String = "",
        var responseMessage: String = "",
        var responseCode: String = "",
        var AID: String = "",
        var code: String = "",
        var telephone: String = "",
        var time: Long = Date().time) : RealmObject(), Parcelable {



    /**
     * This function transforms this instance of transaction
     * log to it corresponding [TransactionResult]
     *
     * @return  the transaction result that was captured by the log
     * @see [TransactionResult]
     */
    internal fun toResult(): TransactionResult {
        val paymentType = when (this.paymentType) {
            PaymentType.PayCode.ordinal -> PaymentType.PayCode
            PaymentType.USSD.ordinal -> PaymentType.USSD
            PaymentType.QR.ordinal -> PaymentType.QR
            else -> PaymentType.Card
        }

        val cardType = when (this.cardType) {
            CardType.VERVE.ordinal -> CardType.VERVE
            CardType.MASTER.ordinal -> CardType.MASTER
            CardType.VISA.ordinal -> CardType.VISA
            CardType.AMERICANEXPRESS.ordinal -> CardType.AMERICANEXPRESS
            CardType.CHINAUNIONPAY.ordinal -> CardType.CHINAUNIONPAY
            else -> CardType.None
        }

        val type = TransactionType.values().first { it.ordinal == this.transactionType }

        return TransactionResult(
                paymentType,
                stan,
                dateTime,
                amount,
                type,
                cardPan,
                cardType,
                cardExpiry,
                authorizationCode,
                pinStatus,
                responseMessage,
                responseCode,
                AID,
                code,
                telephone,
                "",
                "",
                "",
                "",
                ""
        )
    }

    companion object {

        /**
         * This function transforms a [TransactionResult] to
         * a TransactionLog
         *
         * @param result  the transaction result to be logged
         * @return   the transformed transaction log containing the result
         */
        internal fun fromResult(result: TransactionResult) = TransactionLog(
                paymentType = result.paymentType.ordinal,
                stan = result.stan,
                dateTime = result.dateTime,
                amount = result.amount,
                transactionType = result.type.ordinal,
                cardType = result.cardType.ordinal,
                cardExpiry = result.cardExpiry,
                authorizationCode = result.authorizationCode,
                pinStatus = result.pinStatus,
                responseMessage = result.responseMessage,
                responseCode = result.responseCode,
                AID = result.AID,
                code = result.code,
                telephone = result.telephone)


    }
}