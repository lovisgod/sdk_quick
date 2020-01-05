package com.interswitch.smartpos.emv.telpo

import android.content.Context
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.telpo.tps550.api.printer.UsbThermalPrinter

class TelpoDevicePrinterImpl constructor(context: Context) : DevicePrinter {

    private val SCREEN_LARGE_LENGTH = 24
    private val SCREEN_NORMAL_LENGTH = 32

    // font sizes
//    private val NORMAL_FONT = Pair(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16)
//    private val LARGE_FONT = Pair(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_32)

    private val printer by lazy { UsbThermalPrinter(context) }

    private val line: String = "-".repeat(SCREEN_NORMAL_LENGTH)

    override fun printSlip(slip: List<PrintObject>, user: UserType): PrintStatus {
        return PrintStatus.Ok("Printed")
    }

    override fun canPrint(): PrintStatus {
        return PrintStatus.Ok("Can Print")
    }
}