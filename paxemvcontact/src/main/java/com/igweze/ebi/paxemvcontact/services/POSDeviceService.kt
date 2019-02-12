package com.igweze.ebi.paxemvcontact.services

import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice

class POSDeviceService private constructor(
        override val printer: IPrinter,
        override val emvCardTransaction: EmvCardTransaction): POSDevice {

    companion object {
        @JvmStatic
        fun create(printer: IPrinter = Printer, emv: EmvCardTransaction = EmvTransactionService()) = POSDeviceService(printer, emv)


        @JvmStatic
        fun create(): POSDeviceService {
            val printer: IPrinter = Printer;
            val emv: EmvCardTransaction = EmvTransactionService()
            return POSDeviceService(printer, emv)
        }
    }
}