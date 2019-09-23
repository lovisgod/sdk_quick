package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentModel (
    var amount: String? = null,
    var type: Int? = null
): Parcelable {

    fun newPayment(block: PaymentModel.() -> Unit) {
        this.apply(block)
    }
}

fun payment(block: PaymentModel.() -> Unit): PaymentModel {
    return PaymentModel().apply(block)
}