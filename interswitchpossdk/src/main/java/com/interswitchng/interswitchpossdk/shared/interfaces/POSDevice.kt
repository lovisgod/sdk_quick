package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject

interface POSDevice {

    fun attachCallback(callback: CardInsertedCallback)

    fun detachCallback(callback: CardInsertedCallback)

    fun printSlip(slip: List<PrintObject>, user: String)

    fun checkPin(string: String)
}