package com.interswitchng.smartpos.shared.models.core

import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType

data class PurchaseResult(
        val responseCode: String?,
        val responseMessage: String?,
        val purchaseMethod: PaymentType,
        val cardType: CardType,
        val transactionReference: String?): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt().getMethod(),
            parcel.readInt().getCardType(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
        parcel.writeInt(purchaseMethod.ordinal)
        parcel.writeInt(cardType.ordinal)
        parcel.writeString(transactionReference)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PurchaseResult> {
        override fun createFromParcel(parcel: Parcel): PurchaseResult {
            return PurchaseResult(parcel)
        }

        override fun newArray(size: Int): Array<PurchaseResult?> {
            return arrayOfNulls(size)
        }

        private fun Int.getMethod() = when (this) {
            PaymentType.Card.ordinal -> PaymentType.Card
            PaymentType.QR.ordinal -> PaymentType.QR
            PaymentType.PayCode.ordinal -> PaymentType.PayCode
            else -> PaymentType.USSD
        }

        private fun Int.getCardType() = when (this) {
            CardType.AMERICANEXPRESS.ordinal -> CardType.AMERICANEXPRESS
            CardType.MASTER.ordinal -> CardType.MASTER
            CardType.VERVE.ordinal -> CardType.VISA
            CardType.VISA.ordinal -> CardType.VISA
            else -> CardType.None
        }
    }


}