package com.interswitchng.smartpos.shared.interfaces.library

import com.igweze.ebi.simplecalladapter.SimpleHandler
import com.interswitchng.smartpos.shared.interfaces.network.TransactionRequeryCallback
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.utils.IswDisposable
import java.util.concurrent.ExecutorService


typealias Callback<R> = (response: R?, t: Throwable?) -> Unit

internal interface Payable {

    fun initiateQrPayment(request: CodeRequest, callback: Callback<CodeResponse>)

    fun initiateUssdPayment(request: CodeRequest, callback: Callback<CodeResponse>)

    fun getBanks(callback: Callback<List<Bank>?>)

    fun checkPayment(type: PaymentType, status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback): IswDisposable
    // other two
}