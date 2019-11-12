package com.interswitchng.smartpos.shared.interfaces.retrofit

import com.igweze.ebi.simplecalladapter.Simple
import com.interswitchng.smartpos.shared.models.kimono.request.CallHomeModel
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.models.kimono.request.PurchaseRequest
import retrofit2.http.Body
import retrofit2.http.POST

internal interface IKimonoHttpService {

    @POST(Constants.KIMONO_CALL_HOME)
    fun callHome(@Body data: CallHomeModel): Simple<String>

    @POST(Constants.KIMONO_CALL_HOME)
    fun purchaseRequest(@Body data: PurchaseRequest): Simple<String>


}