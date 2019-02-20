package com.interswitchng.smartpos.mockservices

import com.interswitchng.smartpos.shared.interfaces.device.IPrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject

class Printer: IPrinter {
    override fun printSlip(slip: List<PrintObject>, user: UserType) {

    }

}