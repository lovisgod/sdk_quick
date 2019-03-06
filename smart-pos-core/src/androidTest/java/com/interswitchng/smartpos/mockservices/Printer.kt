package com.interswitchng.smartpos.mockservices

import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus

class Printer: DevicePrinter {
    override fun canPrint(): PrintStatus {
        return PrintStatus.Ok("Success")
    }
    override fun printSlip(slip: List<PrintObject>, user: UserType): PrintStatus {
        return PrintStatus.Ok("Success")
    }

}