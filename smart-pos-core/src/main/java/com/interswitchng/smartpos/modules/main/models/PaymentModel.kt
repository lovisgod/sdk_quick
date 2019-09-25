package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentModel (
    var amount: String? = null,
    var type: Type? = null,
    var paymentType: PaymentType? = null
): Parcelable {

    enum class Type {
        MAKE_PAYMENT, TRANSFER_MONEY, BILL_PAYMENT, CASH_OUT
    }

    enum class PaymentType {
        CARD, QR_CODE, USSD, PAY_CODE
    }

    fun newPayment(block: PaymentModel.() -> Unit) {
        this.apply(block)
    }
}

fun payment(block: PaymentModel.() -> Unit): PaymentModel {
    return PaymentModel().apply(block)
}