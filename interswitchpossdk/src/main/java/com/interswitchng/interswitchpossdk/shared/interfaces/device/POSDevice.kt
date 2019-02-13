package com.interswitchng.interswitchpossdk.shared.interfaces.device


interface POSDevice {

    val printer: IPrinter

    fun getEmvCardTransaction(): EmvCardTransaction

}