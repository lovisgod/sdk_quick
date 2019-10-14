package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionSuccessModel (
    var customerName: String = "",
    var amount: Int = 0
): Parcelable