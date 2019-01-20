package com.interswitchng.interswitchpossdk.shared.interfaces

import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject

internal interface POSDevice {

    fun attachCallback(callback: CardInsertedCallback)

    fun detachCallback(callback: CardInsertedCallback)

    fun printReceipt(printSlip: List<PrintObject>)

    fun checkPin(string: String)
}