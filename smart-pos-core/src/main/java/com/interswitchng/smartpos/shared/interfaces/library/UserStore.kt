package com.interswitchng.smartpos.shared.interfaces.library

internal interface UserStore {
    fun <T> getToken(callback: (String) -> T): T
}