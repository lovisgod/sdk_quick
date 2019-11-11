package com.interswitchng.smartpos.modules.main.models

import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType

data class TransactionResultModel(
    val paymentType: PaymentType,
    val stan: String,
    val dateTime: String,
    val amount: String,
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(paymentType.ordinal)
        parcel.writeString(stan)
        parcel.writeString(dateTime)
        parcel.writeString(amount)
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



    companion object CREATOR : Parcelable.Creator<TransactionResultModel> {
        override fun createFromParcel(parcel: Parcel): TransactionResultModel {
            return TransactionResultModel(parcel)
        }

        override fun newArray(size: Int): Array<TransactionResultModel?> {
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

        private fun getCardType(ordinal: Int) = when (ordinal) {
            CardType.VISA.ordinal -> CardType.VISA
            CardType.VERVE.ordinal -> CardType.VERVE
            CardType.MASTER.ordinal -> CardType.MASTER
            CardType.AMERICANEXPRESS.ordinal -> CardType.AMERICANEXPRESS
            else -> CardType.None
        }
    }
}