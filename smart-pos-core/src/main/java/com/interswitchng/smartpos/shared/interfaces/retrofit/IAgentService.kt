package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.interswitchng.smartpos.shared.services.kimono.models.AgentIdResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface IAgentService {

    @Headers("Content-Type: application/json", "Accept: application/json", "Accept-Charset: utf-8")
    @GET
    fun getAgentId(@Url endpoint: String): Call<AgentIdResponse>
}