package com.interswitchng.smartpos.shared.models.transaction

import android.os.Parcel
import android.os.Parcelable
import com.interswitchng.smartpos.IswPos


/**
 * This class represents the purchase request
 * triggered by external source that depends the SDK
 */
internal class PaymentInfo: Parcelable {

    val amount: Int
    var bankCode: String
    lateinit var currentStan: String

    var originalStanId:String? = ""
    var originalAuthId:String? = ""

    constructor(amount: Int, bankCode: String?,originalStanId:String?="",originalAuthId:String?="") {
        this.amount = amount*100
        this.bankCode = bankCode  ?: ""
        this.originalStanId=originalStanId
        this.originalAuthId=originalAuthId
    }

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString())

    fun getStan() = IswPos.getNextStan().also { currentStan = it }

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