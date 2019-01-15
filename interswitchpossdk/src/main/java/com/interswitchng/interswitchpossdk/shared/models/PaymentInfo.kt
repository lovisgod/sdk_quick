package com.interswitchng.interswitchpossdk.shared.models

import android.os.Parcel
import android.os.Parcelable

class PaymentInfo: Parcelable {

    internal val amount: Int
    internal val stan: String
    internal var bankCode: String

    constructor(amount: Int, stan: String?) {
        this.amount = amount
        this.stan = stan ?: ""
        this.bankCode = ""
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())


    internal constructor(amount: Int, stan: String, bankCode: String): this(amount, stan) {
        this.bankCode = bankCode
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(amount)
        parcel.writeString(stan)
        parcel.writeString(bankCode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentInfo> {
        override fun createFromParcel(parcel: Parcel): PaymentInfo {
            return PaymentInfo(parcel)
        }

        override fun newArray(size: Int): Array<PaymentInfo?> {
            return arrayOfNulls(size)
        }
    }
}