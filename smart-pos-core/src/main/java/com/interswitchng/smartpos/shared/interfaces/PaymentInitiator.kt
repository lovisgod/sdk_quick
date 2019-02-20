package com.interswitchng.smartpos.shared.interfaces

import com.interswitchng.smartpos.shared.utilities.Simple
import retrofit2.http.Body
import retrofit2.http.POST

internal interface PaymentInitiator {

    @POST("transactions/qr/do-transaction")
    fun initiateQr(@Body payment: PaymentRequest): Simple<String>

    @POST("transactions/qr/do-transaction")
    fun initiateUSSD(@Body payment: PaymentRequest): Simple<String>
}





data class PaymentRequest(val qrCodeId: Long, val amount: Int,
                          val currencyCode: Int, val authorizationId: Int,
                          val transactionReference: String)