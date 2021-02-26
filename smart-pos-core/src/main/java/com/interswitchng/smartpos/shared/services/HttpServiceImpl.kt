package com.interswitchng.smartpos.shared.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryRequestHeaderModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.modules.main.transfer.utils.CipherUtil
import com.interswitchng.smartpos.modules.main.transfer.utils.HashUtils
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.retrofit.IHttpService
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus

import com.interswitchng.smartpos.shared.utilities.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class HttpServiceImpl(private val httpService: IHttpService) : HttpService {

    val logger by lazy { Logger.with(this.javaClass.name) }

    override suspend fun getBanks(): Optional<List<Bank>> {
        val banks = httpService.getBanks().await()
        val banksResponse = banks.first
        return when (banksResponse) {
            null -> None
            else ->  Some(banksResponse)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun nameEnquiry(
            parameters: NameEnquiryRequestHeaderModel,
            bankCode: String,
            accountNumber: String): Optional<NameEnquiryResponse> {
        val clientId = "IKIA9386DDAE1F2B112CE236CAA472A80A90F99B3987"
        val clientSecret = "E5jlYmDMw3nsPiNMI1Ys8fpmmHa6YRPEu675q6b6iFs"
        val valUrl = "/api/v1/nameenquiry/banks/057/accounts/2150042682"
        val nonce = HashUtils.generateGuid(8)
        val plainCipher = CipherUtil.generateSignatureCipherPlain("GET",valUrl,nonce,clientId,clientSecret)
        parameters.nonce = nonce
        parameters.signature = CipherUtil.generateCipherHash(plainCipher)
        parameters.signatureMethod = "SHA1"
        parameters.host = "saturn.interswitch.com"

        val nameEnquiry = httpService.nameEnquiry(
               url = Constants.SATURN_END_POINT + "v1/nameenquiry/banks/$bankCode/accounts/$accountNumber",
               authorisation =  parameters.authorisation,
               clientId =  parameters.clientId,
               clientSecret = parameters.clientSecret,
               signature = parameters.signature,
               signatureMethod = parameters.signatureMethod,
               timeStamp = parameters.timeStamp,
               nonce = parameters.nonce,
               host = parameters.host
        ).await()
        val nameEnquiryResponse = nameEnquiry.first
        return when (nameEnquiryResponse) {
            null -> None
            else -> Some(nameEnquiryResponse)
        }
    }

    override suspend fun initiateQrPayment(request: CodeRequest): Optional<CodeResponse> {
        val code = httpService.getQrCode(request).await()
        val codeResponse = code.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }

    override suspend fun initiateUssdPayment(request: CodeRequest): Optional<CodeResponse> {
        val response = httpService.getUssdCode(request).await()
        val codeResponse = response.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }

    override suspend fun checkPayment(type: PaymentType, status: TransactionStatus): PaymentStatus {

        // check status based on the transaction type
        val transactionResponse = when (type) {
            PaymentType.USSD -> httpService.getUssdTransactionStatus(status.merchantCode, status.reference).await()
            else -> httpService.getQrTransactionStatus(status.merchantCode, status.reference).await()
        }

        val transaction = transactionResponse.first
        return when {
            transaction == null || transaction.isError() -> PaymentStatus.Error(transaction, transactionResponse.second)
            transaction.isCompleted() -> PaymentStatus.Complete(transaction)
            transaction.isPending() -> PaymentStatus.Pending(transaction)
            else -> {
                // some other error occurred
                // terminate loop and return
                PaymentStatus.Error(transaction, transactionResponse.second)
            }
        }
    }


     suspend fun callHome(request: CodeRequest): Optional<CodeResponse> {
       //send XML and Receive XML

        val response = httpService.getUssdCode(request).await()
        val codeResponse = response.first
        return when (codeResponse) {
            null -> None
            else -> Some(codeResponse)
        }
    }



    private suspend fun <T> Simple<T>.await(): Pair<T?, String?> {
        return suspendCoroutine { continuation ->
            process { response, t ->
                val message =  t?.message ?: t?.localizedMessage

                // log errors
                if (message != null) logger.log(message)
                // pair result and error
                val result = Pair(response, message)
                // return response
                continuation.resume(result)
            }
        }
    }
}