package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.Constants.TILL_BANKS_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_CODE_END_POINT
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_TRANSACTION_STATUS_QR
import com.interswitchng.interswitchpossdk.shared.Constants.TILL_TRANSACTION_STATUS_USSD
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import retrofit2.http.*

internal interface IHttpService {

    @POST(TILL_CODE_END_POINT)
    fun getQrCode(@Body request: CodeRequest): Simple<CodeResponse>

    @POST(TILL_CODE_END_POINT)
    fun getUssdCode(@Body request: CodeRequest): Simple<CodeResponse>

    @GET(TILL_TRANSACTION_STATUS_QR)
    fun getQrTransactionStatus(@Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String): Simple<Transaction?>


    @GET(TILL_TRANSACTION_STATUS_USSD)
    fun getUssdTransactionStatus(@Query("merchantCode") merchantCode: String,
                             @Query("transactionReference") transactionReference: String): Simple<Transaction?>

    @GET(TILL_BANKS_END_POINT)
    fun getBanks(): Simple<List<Bank>?>
}