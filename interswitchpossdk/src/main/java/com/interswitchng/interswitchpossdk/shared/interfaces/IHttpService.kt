package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.Constants.TILL_BANKS_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_CODE_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_TRANSACTION_STATUS
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import retrofit2.http.*

internal interface IHttpService {

    @POST(TILL_CODE_END_POINT)
    fun getQrCode(@Body request: CodeRequest): Simple<CodeResponse>

    @POST(TILL_CODE_END_POINT)
    fun getUssdCode(@Body request: CodeRequest): Simple<CodeResponse>

    @GET(TILL_TRANSACTION_STATUS)
    fun getTransactionStatus(@Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String): Simple<Transaction?>

    @GET(TILL_BANKS_END_POINT)
    fun getBanks(): Simple<List<Bank>?>
}