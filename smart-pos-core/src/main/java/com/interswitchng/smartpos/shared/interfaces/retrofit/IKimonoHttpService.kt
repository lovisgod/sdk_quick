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
    @POST
    fun makeCashOutInquiry(@Url url: String, @Body purchaseRequest: RequestBody): Simple<BillPaymentResponse>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST
    fun makeBillPaymentInquiry(@Url url: String, @Header("Authorization") token: String,  @Body purchaseRequest: RequestBody): Simple<BillPaymentResponse>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST
    fun makeCashOutPayment(@Url url: String, @Body purchaseRequest: RequestBody,  @Header("Authorization") token: String): Simple<BillPaymentResponse>


    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST
    fun makeCashOutPaymentnew(@Url url: String, @Body purchaseRequest: RequestBody): Simple<BillPaymentResponse>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun refund(@Body refundRequest: RequestBody): Simple<ResponseBody>


    @Headers("Content-Type: application/json", "Accept: application/json", "Accept-Charset: utf-8")
    @GET
    fun getAgentId(@Url endpoint: String): retrofit2.Response<AgentIdResponse>

    @Headers("Content-Type: text/xml", "Accept: application/xml", "Accept-Charset: utf-8")
    @POST(Constants.KIMONO_END_POINT)
    fun makeTransfer(@Body purchaseRequest: RequestBody, @Header("Authorization") token: String): Simple<BillPaymentResponse>



}