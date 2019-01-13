package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.Constants.TILL_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_TRANSACTION_STATUS
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import retrofit2.http.*

internal interface IHttpService {

    @POST(TILL_END_POINT)
    fun getQrCode(@Body request: CodeRequest): Simple<CodeResponse>

    @POST(TILL_END_POINT)
    fun getUssdCode(@Body request: CodeRequest): Simple<CodeResponse>

    @GET("$TILL_TRANSACTION_STATUS/{transactionType}")
    fun getTransactionStatus(@Path("transactionType") transactionType: String,
                             @Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String)
}