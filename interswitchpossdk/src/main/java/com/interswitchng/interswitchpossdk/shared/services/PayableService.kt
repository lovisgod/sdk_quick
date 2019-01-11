package com.interswitchng.interswitchpossdk.shared.services

import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PayableCodeCallback
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo

internal class PayableService: Payable {
    override fun initiateQrPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback) {
        TODO("not implemented")
    }

    override fun initiateUssdPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback) {
        TODO("not implemented")
    }

}