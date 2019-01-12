package com.interswitchng.interswitchpossdk.mockservices

import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PayableCodeCallback
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo

internal class MockPayableService: Payable {

    override fun initiateUssdPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initiateQrPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}