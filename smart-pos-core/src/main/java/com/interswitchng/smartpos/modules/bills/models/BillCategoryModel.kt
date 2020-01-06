package com.interswitchng.smartpos.modules.bills.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BillCategoryModel(
    var name: String? = null,
    var id: String? = null,
    var bills: ArrayList<BillModel> = ArrayList<BillModel>()
) : Parcelable

fun bill(block: BillCategoryModel.() -> Unit): BillCategoryModel {
    return BillCategoryModel().apply(block)
}