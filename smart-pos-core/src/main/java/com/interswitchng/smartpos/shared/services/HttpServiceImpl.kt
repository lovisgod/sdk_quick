package com.interswitchng.smartpos.shared.services

import com.interswitchng.smartpos.shared.interfaces.retrofit.IHttpService
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.interfaces.library.TransactionRequeryCallback
import com.interswitchng.smartpos.shared.models.core.Callback
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.utils.IswDisposable
import com.interswitchng.smartpos.shared.utilities.ThreadUtils

internal class HttpServiceImpl(private val httpService: IHttpService): HttpService {

    override fun getBanks(callback: Callback<List<Bank>?>) {
        httpService.getBanks().process(callback)
    }

    override fun initiateQrPayment(request: CodeRequest, callback: Callback<CodeResponse>) {
        httpService.getQrCode(request).process(callback)
    }

    override fun initiateUssdPayment(request: CodeRequest, callback: Callback<CodeResponse>) {
        httpService.getUssdCode(request).process(callback)
    }

    override fun checkPayment(type: PaymentType, status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback): IswDisposable {

        var hasResponse = false
        val endTime = System.currentTimeMillis() + timeout
        return ThreadUtils.createExecutor {
            var secs = 1L
            while (!hasResponse && !it.isDisposed) {
                val call = when(type) {
                    PaymentType.USSD -> httpService.getUssdTransactionStatus(status.merchantCode, status.reference)
                    else -> httpService.getQrTransactionStatus(status.merchantCode, status.reference)
                }

                call.run { transaction, throwable ->
                    val currentTime = System.currentTimeMillis()
                    when {
                        throwable != null -> {
                            secs = 0
                            hasResponse = true
                            callback.onTransactionError(transaction, throwable)
                        }
                        transaction == null -> { /* do nothing */ }
                        transaction.isCompleted() -> {
                            callback.onTransactionCompleted(transaction)
                            hasResponse = true
                        }
                        transaction.isPending() && endTime > currentTime -> {
                            callback.onTransactionStillPending(transaction)
                            secs += 2
                        }
                        transaction.isPending() && endTime <= currentTime -> {
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
                val canSleep = !hasResponse && !it.isDisposed && endTime > nextDuration + System.currentTimeMillis()
                if (canSleep) Thread.sleep(nextDuration)
            }
        }
    }
}