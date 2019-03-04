package com.interswitchng.smartpos.shared.interfaces.device


interface POSDevice {

    val printer: DevicePrinter

    fun getEmvCardTransaction(): EmvCardReader

}