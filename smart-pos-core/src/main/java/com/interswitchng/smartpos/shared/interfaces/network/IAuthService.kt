package com.interswitchng.smartpos.shared.interfaces.network

import com.interswitchng.smartpos.shared.Constants.AUTH_END_POINT
import com.interswitchng.smartpos.shared.models.core.AuthToken
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.shared.utilities.Simple
import retrofit2.http.POST

internal interface IAuthService {

    @POST(AUTH_END_POINT)
    fun getToken(credentials: POSConfig): Simple<AuthToken?>
}