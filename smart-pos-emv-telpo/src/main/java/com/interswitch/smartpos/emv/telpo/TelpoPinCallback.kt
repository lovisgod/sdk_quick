package com.interswitch.smartpos.emv.telpo

interface TelpoPinCallback {

    suspend fun showInsertCard()

    val pinResult: Int

    suspend fun enterPin(isOnline: Boolean, triesCount: Int, offlineTriesLeft: Int, panBlock: String)

    suspend fun showPinOk()
}