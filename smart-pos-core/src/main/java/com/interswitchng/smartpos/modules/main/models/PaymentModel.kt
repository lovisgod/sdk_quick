package com.interswitchng.smartpos.modules.main.models

import android.os.Parcelable
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentModel (
        var amount: Int = 0,
        var formattedAmount: String = EMPTY_STRING,
        var type: TransactionType? = null,
        var paymentType: PaymentType? = null,
        var card: CardModel? = null,
        var billPayment: BillPaymentModel? = null,
        var authorizationId: String? = null,
        var stan: String? = null,
        var originalStan: String? = null,
        var originalDateAndTime: String? = null
): Parcelable {

    fun getTransactionStan() = IswPos.getNextStan().also { stan = it }

    enum class Type {
        MAKE_PAYMENT, TRANSFER_MONEY, BILL_PAYMENT, CASH_OUT
    }

    enum class TransactionType {
        CARD_PURCHASE, PRE_AUTHORIZATION, CARD_NOT_PRESENT, COMPLETION, REFUND, ECASH, ECHANGE, REVERSAL, BILL_PAYMENT, CASH_OUT, TRANSFER
    }

    enum class PaymentType {
        CARD, QR_CODE, USSD, PAY_CODE,CARD_NOT_PRESENT
    }

    fun newPayment(block: PaymentModel.() -> Unit) {
        this.apply(block)
    }
}

fun payment(block: PaymentModel.() -> Unit): PaymentModel {
    return PaymentModel().apply(block)
}
