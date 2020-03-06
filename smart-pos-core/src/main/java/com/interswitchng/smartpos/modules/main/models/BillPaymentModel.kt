package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BillPaymentModel(
        var customerId: String? = null,
        var phoneNumber: String? = null,
        var customerEmail: String? = null,
        var paymentCode: String? = null,
        var bankCbnCode: String? = null

) : Parcelable

fun BillPaymentModel(block: BillPaymentModel.() -> Unit): BillPaymentModel {
    return BillPaymentModel().apply(block)
}