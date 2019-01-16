package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.response.Transaction

internal interface TransactionRequeryCallback {

    fun onTransactionCompleted(transaction: Transaction)

    fun onTransactionStillPending(transaction: Transaction)

    fun onTransactionError(transaction: Transaction?, throwable: Throwable?)

    fun onTransactionTimeOut()
}