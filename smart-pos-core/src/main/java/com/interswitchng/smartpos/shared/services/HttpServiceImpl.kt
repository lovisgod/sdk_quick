package com.interswitchng.smartpos.shared.services

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.interfaces.retrofit.IHttpService
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class HttpServiceImpl(private val httpService: IHttpService) : HttpService {

    val logger by lazy { Logger.with(this.javaClass.name) }

    override suspend fun getBanks(): Optional<List<Bank>> {
        val banks = httpService.getBanks().await()
        return when (banks) {
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
        return when (response) {
            null -> None
            else -> Some(response)
        }
    }

    override suspend fun checkPayment(type: PaymentType, status: TransactionStatus): PaymentStatus {

        // check status based on the transaction type
        val transaction: Transaction? = when (type) {
            PaymentType.USSD -> httpService.getUssdTransactionStatus(status.merchantCode, status.reference).await()
            else -> httpService.getQrTransactionStatus(status.merchantCode, status.reference).await()
        }

        return when {
            transaction == null || transaction.isError() -> PaymentStatus.Error(transaction)
            transaction.isCompleted() -> PaymentStatus.Complete(transaction)
            transaction.isPending() -> PaymentStatus.Pending(transaction)
            else -> {
                // some other error occurred
                // terminate loop and return
                PaymentStatus.Error(transaction)
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