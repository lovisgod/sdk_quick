package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse

internal interface Payable {

    fun initiateQrPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback)

    fun initiateUssdPayment(paymentInfo: PaymentInfo, callback: PayableCodeCallback)

    // other two

}

internal typealias PayableCodeCallback = (CodeResponse, Throwable?) -> Unit