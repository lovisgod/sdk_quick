package com.interswitchng.smartpos.modules.bills.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BillModel(
    var name: String? = null,
    var id: String? = null
) : Parcelable


fun bill(block: BillModel.() -> Unit): BillModel {
    return BillModel().apply(block)
}