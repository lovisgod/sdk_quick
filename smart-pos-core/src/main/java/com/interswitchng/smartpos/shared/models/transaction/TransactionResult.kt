package com.interswitchng.smartpos.shared.models.transaction

import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionStatus
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.printer.slips.CardSlip
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.printer.slips.UssdQrSlip
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType


/**
 * This class represents the final result
 * of the triggered purchase transaction.
 * This is what gets returned to calling application
 */
internal data class TransactionResult(
        val paymentType: PaymentType,
        val stan: String,
        val dateTime: String,
        val amount: String,
        val type: TransactionType,
        val cardPan: String,
        val cardType: CardType,
        val cardExpiry: String,
        val authorizationCode: String,
        val pinStatus: String,
        val responseMessage: String,
        val responseCode: String,
        val AID: String,
        val code: String,
        val telephone: String) : Parcelable {


    constructor(parcel: Parcel) : this(
            getPaymentType(parcel.readInt()),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            getTransactionType(parcel.readInt()),
            parcel.readString()!!,
            getCardType(parcel.readInt()),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)


    fun getSlip(terminal: TerminalInfo): TransactionSlip {
        return when (paymentType) {
            PaymentType.USSD, PaymentType.QR -> UssdQrSlip(terminal, getTransactionStatus(), getTransactionInfo())
            PaymentType.Card, PaymentType.PayCode -> CardSlip(terminal, getTransactionStatus(), getTransactionInfo())
        }
    }


    /// function to extract
    /// print slip transaction info
    fun getTransactionInfo() =
            TransactionInfo(
                    paymentType,
                    stan,
                    dateTime,
                    amount,
                    type,
                    cardPan,
                    cardType.name,
                    cardExpiry,
                    authorizationCode,
                    pinStatus)


    /// function to extract
    /// print slip transaction status
    fun getTransactionStatus() =
            TransactionStatus(
                    responseMessage,
                    responseCode,
                    AID,
                    telephone)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(paymentType.ordinal)
        parcel.writeString(stan)
        parcel.writeString(dateTime)
        parcel.writeString(amount)
        parcel.writeInt(type.ordinal)
        parcel.writeString(cardPan)
        parcel.writeInt(cardType.ordinal)
        parcel.writeString(cardExpiry)
        parcel.writeString(authorizationCode)
        parcel.writeString(pinStatus)
        parcel.writeString(responseMessage)
        parcel.writeString(responseCode)
        parcel.writeString(AID)
        parcel.writeString(code)
        parcel.writeString(telephone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransactionResult> {
        override fun createFromParcel(parcel: Parcel): TransactionResult {
            return TransactionResult(parcel)
        }

        override fun newArray(size: Int): Array<TransactionResult?> {
            return arrayOfNulls(size)
        }

        private fun getPaymentType(ordinal: Int): PaymentType {
            return when (ordinal) {
                PaymentType.Card.ordinal -> PaymentType.Card
                PaymentType.QR.ordinal -> PaymentType.QR
                PaymentType.USSD.ordinal -> PaymentType.USSD
                else -> PaymentType.PayCode
            }
        }

        private fun getTransactionType(ordinal: Int): TransactionType {
            return TransactionType.Purchase
        }

        private fun getCardType(ordinal: Int) = when (ordinal) {
            CardType.VISA.ordinal -> CardType.VISA
            CardType.VERVE.ordinal -> CardType.VERVE
            CardType.MASTER.ordinal -> CardType.MASTER
            CardType.AMERICANEXPRESS.ordinal -> CardType.AMERICANEXPRESS
            else -> CardType.None
        }
    }

}