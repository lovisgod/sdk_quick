package com.interswitchng.interswitchpossdk.shared.interfaces.network

import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction

internal interface TransactionRequeryCallback {

    fun onTransactionCompleted(transaction: Transaction)

    fun onTransactionStillPending(transaction: Transaction)

    fun onTransactionError(transaction: Transaction?, throwable: Throwable?)

    fun onTransactionTimeOut()
}