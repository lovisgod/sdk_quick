package com.interswitchng.smartpos.shared.interfaces.library

import com.interswitchng.smartpos.shared.models.core.Callback
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.utils.IswDisposable


internal interface HttpService {

    fun initiateQrPayment(request: CodeRequest, callback: Callback<CodeResponse>)

    fun initiateUssdPayment(request: CodeRequest, callback: Callback<CodeResponse>)

    fun getBanks(callback: Callback<List<Bank>?>)

    fun checkPayment(type: PaymentType, status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback): IswDisposable
    // other two
}