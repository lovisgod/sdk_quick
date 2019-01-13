package com.interswitchng.interswitchpossdk.shared.services

import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PayableResponseHandler
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse

internal class PayableService(private val httpService: IHttpService): Payable {

    override fun initiateQrPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>) {
        httpService.getQrCode(request).process(callback)
    }

    override fun initiateUssdPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>) {
        httpService.getUssdCode(request).process(callback)
    }

}