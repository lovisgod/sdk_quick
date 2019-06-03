package com.interswitchng.smartpos.shared.models.transaction


/**
 * This enum class represents the different
 * payment methods the SDK supports
 */
enum class PaymentType {
    PayCode,
    Card,
    QR,
    USSD;

    override fun toString(): String {
        val string =  when {
            this == Card -> "Credit/Debit Card"
            this == QR -> "QR Code"
            else -> super.toString()
        }

        return string.toUpperCase()
    }
}