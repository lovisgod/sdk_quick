package com.interswitchng.smartpos.shared.services

import com.interswitchng.smartpos.shared.interfaces.network.IHttpService
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import com.interswitchng.smartpos.shared.interfaces.network.TransactionRequeryCallback
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.utilities.SimpleResponseHandler
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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

    override fun checkPayment(type: PaymentType, status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback): ExecutorService {
        val executor = Executors.newSingleThreadScheduledExecutor()

        var hasResponse = false
        executor.execute {
            var secs = 1L
            var elapsedTime = secs
            while (!hasResponse) {
                val call = when(type) {
                    PaymentType.USSD -> httpService.getUssdTransactionStatus(status.merchantCode, status.reference)
                    else -> httpService.getQrTransactionStatus(status.merchantCode, status.reference)
                }

                call.test { transaction, throwable ->
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
        }

        return executor
    }
}