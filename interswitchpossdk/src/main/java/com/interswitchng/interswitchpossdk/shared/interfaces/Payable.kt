package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler

internal interface Payable {

    fun initiateQrPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>)

    fun initiateUssdPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>)

    fun getBanks(callback: SimpleResponseHandler<List<Bank>?>)

    fun checkPayment(status: TransactionStatus, timeout: Long, callback: TransactionRequeryCallback)
    // other two
}