package com.interswitchng.smartpos.shared.interfaces.device

import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus

interface DevicePrinter {

    fun printSlip(slip: List<PrintObject>, user: UserType): PrintStatus

    fun canPrint(): PrintStatus

}