package com.interswitch.smartpos.emv.telpo

import com.telpo.emv.EmvPinData

interface TelpoPinCallback {

    suspend fun showInsertCard()

    val pinResult: Int

    suspend fun enterPin(isOnline: Boolean, triesCount: Int, offlineTriesLeft: Int, panBlock: String, pinData: EmvPinData?)

    suspend fun showPinOk()
}