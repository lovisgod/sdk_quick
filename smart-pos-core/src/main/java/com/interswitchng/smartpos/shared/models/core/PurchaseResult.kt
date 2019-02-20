package com.interswitchng.smartpos.shared.models.core

import android.os.Parcel
import android.os.Parcelable

data class PurchaseResult(
        val responseCode: String?,
        val responseMessage: String?,
        val transactionReference: String?): Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(responseCode)
        parcel.writeString(responseMessage)
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
    }


}