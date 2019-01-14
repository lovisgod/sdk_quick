package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse

internal interface Payable {

    fun initiateQrPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>)

    fun initiateUssdPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>)

    // other two

    fun getBanks(callback: PayableResponseHandler<List<Bank>?>)
}

internal typealias PayableResponseHandler<T> = (T, Throwable?) -> Unit