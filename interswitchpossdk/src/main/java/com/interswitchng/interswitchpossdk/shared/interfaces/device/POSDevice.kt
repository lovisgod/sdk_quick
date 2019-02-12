package com.interswitchng.interswitchpossdk.shared.interfaces.device


interface POSDevice {

    val emvCardTransaction: EmvCardTransaction

    val printer: IPrinter

}