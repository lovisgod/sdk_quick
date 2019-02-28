package com.interswitchng.smartpos.emv.pax.emv

interface PinCallback {

    fun showInsertCard()

    fun getPinResult(panBlock: String): Int

    fun enterPin(isOnline: Boolean, triesCount: Int, offlineTriesLeft: Int, panBlock: String)

    fun showPinOk()
}