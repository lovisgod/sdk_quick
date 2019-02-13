package com.igweze.ebi.paxemvcontact.services

import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice

class POSDeviceService private constructor(
        override val printer: IPrinter, private val factory: () -> EmvCardTransaction): POSDevice {

    override fun getEmvCardTransaction(): EmvCardTransaction = factory()


    companion object {
        @JvmStatic
        fun create(printer: IPrinter = Printer, factory: () -> EmvCardTransaction) = POSDeviceService(printer, factory)


        @JvmStatic
        fun create(): POSDeviceService {
            val printer: IPrinter = Printer;
            val factory = { EmvTransactionService() }
            return POSDeviceService(printer, factory)
        }
    }
}