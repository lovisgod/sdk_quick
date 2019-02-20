package com.interswitchng.smartpos.shared.interfaces.device

import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject

interface IPrinter {

    fun printSlip(slip: List<PrintObject>, user: UserType)

}