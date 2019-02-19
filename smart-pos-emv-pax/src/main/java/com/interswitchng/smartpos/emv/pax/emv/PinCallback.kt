package com.interswitchng.smartpos.emv.pax.emv

interface PinCallback {

    fun showInsertCard()

    fun getPinResult(panBlock: String): Int

    fun enterPin(isOnline: Boolean, offlineTriesLeft: Int, panBlock: String)

    fun showPinOk()
}