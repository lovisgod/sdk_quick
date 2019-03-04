package com.interswitchng.smartpos.shared.interfaces.library

import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction

internal interface TransactionRequeryCallback {

    fun onTransactionCompleted(transaction: Transaction)

    fun onTransactionStillPending(transaction: Transaction)

    fun onTransactionError(transaction: Transaction?, throwable: Throwable?)

    fun onTransactionTimeOut()
}