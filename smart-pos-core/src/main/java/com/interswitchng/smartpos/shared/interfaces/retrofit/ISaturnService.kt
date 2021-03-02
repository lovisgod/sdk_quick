package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.modules.main.transfer.models.BeneficiaryModel
import com.interswitchng.smartpos.modules.main.transfer.models.NameEnquiryResponse
import com.interswitchng.smartpos.shared.services.kimono.models.AgentIdResponse
import com.interswitchng.smartpos.shared.services.kimono.models.AllTerminalInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ISaturnService {

    @GET
    fun nameEnquiry(
            @Url url: String,
            @Header("Authorization") authorisation: String?,
            @Header("clientSecret") clientSecret: String?,
            @Header("clientId") clientId: String?,
            @Header("Signature") signature: String?,
            @Header("SignatureMethod") signatureMethod: String?,
            @Header("Timestamp") timeStamp: String?,
            @Header("Nonce") nonce: String?,
            @Header("Host") host: String?
    ): Simple<BeneficiaryModel?>


}