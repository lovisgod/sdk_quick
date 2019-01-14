package com.interswitchng.interswitchpossdk.shared.models

import android.os.Parcel
import android.os.Parcelable

data class PaymentInfo (
    internal val amount: Int,
    internal val bankCode: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(amount)
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