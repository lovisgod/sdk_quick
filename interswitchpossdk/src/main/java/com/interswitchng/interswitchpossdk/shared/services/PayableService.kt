package com.interswitchng.interswitchpossdk.shared.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
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

    override fun checkPayment(transaction: TransactionStatus, callback: SimpleResponseHandler<Transaction?>) {
        httpService.getTransactionStatus(transaction.type, transaction.merchantCode, transaction.reference)
                .process(callback)
    }

}