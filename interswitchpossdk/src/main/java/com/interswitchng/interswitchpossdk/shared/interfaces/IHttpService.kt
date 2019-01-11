package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.Constants.TILL_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_TRANSACTION_STATUS
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface IHttpService {

    @POST(TILL_END_POINT)
    fun getQrCode(): Call<CodeResponse>

    @POST(TILL_END_POINT)
    fun getUssdCode(): Call<CodeResponse>

    @GET("$TILL_TRANSACTION_STATUS/{transactionType}")
    fun getTransactionStatus(@Path("transactionType") transactionType: String,
                             @Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String)
}