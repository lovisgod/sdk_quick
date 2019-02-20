package com.interswitchng.smartpos.shared.interfaces.network

import com.interswitchng.smartpos.shared.Constants.TILL_BANKS_END_POINT
import com.interswitchng.smartpos.shared.Constants.TILL_CODE_END_POINT
import com.interswitchng.smartpos.shared.Constants.TILL_TRANSACTION_STATUS_QR
import com.interswitchng.smartpos.shared.Constants.TILL_TRANSACTION_STATUS_USSD
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.Simple
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