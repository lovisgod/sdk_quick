package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.services.kimono.models.CallHomeRequest
import com.interswitchng.smartpos.shared.services.kimono.models.KimonoKeyRequest
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface IKimonoHttpService {


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun callHome(@Body data: CallHomeRequest): Simple<ResponseBody>



    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun completion(@Body data: RequestBody): Simple<ResponseBody>


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun reversePurchase(@Body reverseRequest: RequestBody): Simple<ResponseBody>
//    fun reversePurchase(@Body reverseRequest: ReversalRequest): Simple<PurchaseResponse>



    @POST(Constants.KIMONO_END_POINT)
    fun reservation(@Body data: RequestBody): Simple<ResponseBody>
//    fun reservation(@Body data: ReservationRequest): Simple<ReservationResponse>



    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun makePurchase(@Body purchaseRequest: RequestBody): Simple<ResponseBody>
//    fun makePurchase(@Body purchaseRequest: PurchaseRequest): Simple<ResponseBody>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_CASH_OUT_ENDPOINT_INQUIRY)
    fun makeCashOutInquiry(@Body purchaseRequest: RequestBody): Simple<ResponseBody>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_CASH_OUT_ENDPOINT_PAY)
    fun makeCashOutPayment(@Body purchaseRequest: RequestBody): Simple<ResponseBody>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun refund(@Body refundRequest: RequestBody): Simple<ResponseBody>


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST("")
    fun getPinKey(@Url endpoint: String,
            @Body request: KimonoKeyRequest): Simple<ResponseBody>


    @Headers("Content-Type: application/xml")
    @GET(Constants.KIMONO_KEY_END_POINT)
    fun getKimonoKey(@Query("cmd") cmd: String,
                     @Query("terminal_id") terminalId: String,
                     @Query("pkmod") pkmod: String,
                     @Query("pkex") pkex: String,
                     @Query("pkv") pkv: String,
                     @Query("keyset_id") keyset_id: String,
                     @Query("der_en") der_en: String): Simple<ResponseBody>


}