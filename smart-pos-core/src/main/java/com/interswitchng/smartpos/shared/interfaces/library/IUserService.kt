package com.interswitchng.smartpos.shared.interfaces.library

internal interface IUserService {
    fun <T> getToken(callback: (String) -> T): T
}