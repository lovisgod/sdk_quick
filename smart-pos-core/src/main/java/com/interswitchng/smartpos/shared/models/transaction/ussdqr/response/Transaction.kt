package com.interswitchng.smartpos.shared.models.transaction.ussdqr.response

internal class Transaction (
    internal val id: Int,
    internal val amount: Int,
    internal val transactionReference: String,
    internal val responseCode: String,
    internal val currencyCode: String?,
    internal val paymentCancelled: Boolean,
    internal val bankCode: String?,
    internal val remittanceAmount: Int,
    internal val responseDescription: String?
) {


    fun isCompleted(): Boolean = responseCode == CodeResponse.OK
    fun isPending(): Boolean = responseCode == CodeResponse.PENDING
    fun isSuccessful(): Boolean = isCompleted()

    companion object {
        fun default(): Transaction {
            val int = 0
            val str = ""
            return Transaction(int, int, str, str, str, false, str, 0, str)
        }

    }
}