package com.interswitchng.smartpos.shared.services

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.igweze.ebi.simplecalladapter.Simple
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
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class HttpServiceImpl(private val httpService: IHttpService): HttpService {

    val logger by lazy { Logger.with(this.javaClass.name) }

    override suspend fun getBanks(): Optional<List<Bank>> {
        val banks = httpService.getBanks().await()
        return when(banks) {
            null -> None
            else -> Some(banks)
        }
    }

    override suspend fun initiateQrPayment(request: CodeRequest): Optional<CodeResponse> {
        val code = httpService.getQrCode(request).await()
        return when (code) {
            null -> None
            else -> Some(code)
        }
    }

    override suspend fun initiateUssdPayment(request: CodeRequest): Optional<CodeResponse> {
        val response = httpService.getUssdCode(request).await()
        return when(response) {
            null -> None
            else -> Some(response)
        }
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

    private suspend fun <T> Simple<T>.await(): T? {
        return suspendCoroutine { continuation ->
            process { response, t ->
                // log errors
                if (t != null) logger.log(t.localizedMessage)
                // return response
                continuation.resume(response)
            }
        }
    }
}