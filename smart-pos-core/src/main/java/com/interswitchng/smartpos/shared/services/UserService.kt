package com.interswitchng.smartpos.shared.services

import com.interswitchng.smartpos.shared.interfaces.library.IUserService
import com.interswitchng.smartpos.shared.interfaces.network.IAuthService
import com.interswitchng.smartpos.shared.models.core.AuthToken
import retrofit2.HttpException
import java.io.IOException

internal class UserService(private val authService: IAuthService): IUserService {

    private lateinit var token: AuthToken

    override  fun <T> getToken (callback: (String) -> T): T {
        // define grant type and scope for auth request
        val grantType = "client_credentials"
        val scope = "profile"

        // process callback based on availability of access token
        if (::token.isInitialized) return callback(token.token)
        else authService.getToken(grantType, scope).run().let {
            if (it.isSuccessful) {
                token = it.body()!!
                return callback(token.token)
            } else {
                throw if (it.code() in 400..511) HttpException(it)
                else IOException("Unable to get Access Token")
            }
        }
    }
}