package com.interswitchng.interswitchpossdk.shared.interfaces.device

import com.interswitchng.interswitchpossdk.shared.models.core.UserType
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject

interface IPrinter {

    fun printSlip(slip: List<PrintObject>, user: UserType)

}