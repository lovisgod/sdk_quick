package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.services.kimono.models.*
import com.interswitchng.smartpos.shared.services.kimono.models.KimonoKeyRequest
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseRequest
import com.interswitchng.smartpos.shared.services.kimono.models.PurchaseResponse
import com.interswitchng.smartpos.shared.services.kimono.models.ReversalRequest
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

internal interface IKimonoHttpService {

    @POST(Constants.KIMONO_END_POINT)
    fun callHome(@Body data: CallHomeRequest): Simple<ResponseBody>



    @POST(Constants.KIMONO_END_POINT)
    fun completion(@Body data: CompletionRequest): Simple<CompletionResponse>



    @POST(Constants.KIMONO_END_POINT)
    fun reservation(@Body data: ReservationRequest): Simple<ReservationResponse>



    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun makePurchase(@Body purchaseRequest: PurchaseRequest): Simple<PurchaseResponse>


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun reversePurchase(@Body reverseRequest: ReversalRequest): Simple<PurchaseResponse>


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST("")
    fun getPinKey(@Url endpoint: String,
            @Body request: KimonoKeyRequest): Simple<ResponseBody>

}