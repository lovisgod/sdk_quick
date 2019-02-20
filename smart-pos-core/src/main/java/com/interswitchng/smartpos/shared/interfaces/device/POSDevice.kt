package com.interswitchng.smartpos.shared.interfaces.device


interface POSDevice {

    val printer: IPrinter

    fun getEmvCardTransaction(): EmvCardTransaction

}