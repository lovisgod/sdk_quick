package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.modules.main.transfer.TokenPassportResponse
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.services.kimono.models.AgentIdResponse
import com.interswitchng.smartpos.shared.services.kimono.models.BillPaymentResponse
import com.interswitchng.smartpos.shared.services.kimono.models.CallHomeRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface TokenService {

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST("requesttoken/perform-process")
    fun getToken(@Body purchaseRequest: RequestBody): Simple<TokenPassportResponse>


}