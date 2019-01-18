package com.interswitchng.interswitchpossdk.shared.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.TransactionRequeryCallback
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler

internal class PayableService(private val httpService: IHttpService): Payable {

    override fun getBanks(callback: SimpleResponseHandler<List<Bank>?>) {
        httpService.getBanks().process(callback)
    }

    override fun initiateQrPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>) {
        httpService.getQrCode(request).process(callback)
    }

    override fun initiateUssdPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>) {
        httpService.getUssdCode(request).process(callback)
    }

    override fun checkPayment(status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback) {
        var hasResponse = false
        Thread(Runnable {
            var secs = 1L
            var elapsedTime = secs
            while (!hasResponse) {
                httpService.getTransactionStatus(status.type, status.merchantCode, status.reference).test { transaction, throwable ->
                    when {
                        throwable != null -> {
                            secs = 0
                            hasResponse = true
                            callback.onTransactionError(transaction, throwable)
                        }
                        transaction == null -> {

                        }
                        transaction.isCompleted() -> {
                            callback.onTransactionCompleted(transaction)
                            hasResponse = true
                        }
                        transaction.isPending() && timeout > elapsedTime -> {
                            callback.onTransactionStillPending(transaction)
                            secs += 2
                        }
                        transaction.isPending() && timeout <= elapsedTime -> {
                            secs = 0
                            hasResponse = true
                            callback.onTransactionTimeOut()
                        }
                        else -> {
                            // some other error occurred
                            // terminate loop and return
                            secs = 0
                            hasResponse = true
                            callback.onTransactionError(transaction, throwable)
                        }
                    }
                }

                val nextDuration = secs * 1000
                elapsedTime += nextDuration
                if (!hasResponse) Thread.sleep(nextDuration)
            }
        }).start()
    }
}