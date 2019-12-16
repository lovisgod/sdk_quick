package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentModel (
    var amount: Int = 0,
    var formattedAmount: String = EMPTY_STRING,
    var type: TransactionType? = null,
    var paymentType: PaymentType? = null,
    var card: CardModel? = null,
    var authorizationId: String? = null,
    var originalStan: String? = null,
    var originalDateAndTime: String? = null
): Parcelable {

    enum class Type {
        MAKE_PAYMENT, TRANSFER_MONEY, BILL_PAYMENT, CASH_OUT
    }

    enum class TransactionType {
        CARD_PURCHASE, PRE_AUTHORIZATION, CARD_NOT_PRESENT, COMPLETION, REFUND
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
