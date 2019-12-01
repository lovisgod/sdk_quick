package com.interswitch.smartpos.emv.telpo

import android.content.Context
import android.media.ThumbnailUtils
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.telpo.tps550.api.TelpoException
import com.telpo.tps550.api.printer.UsbThermalPrinter

class TelpoDevicePrinterImpl constructor(context: Context) : DevicePrinter {

    private val printer = UsbThermalPrinter(context)

    private val line: String = "-".repeat(SCREEN_NORMAL_LENGTH)

    override fun printSlip(slip: List<PrintObject>, user: UserType): PrintStatus {
        printer.apply {
            reset()
            setMonoSpace(true)
            setAlgin(UsbThermalPrinter.ALGIN_LEFT)
        }
        //Print the company's logo
        val printBitmap = ThumbnailUtils.extractThumbnail(TelpoPOSDeviceImpl.companyLogo, 244, 116)
        printer.printLogo(printBitmap, true)

        for (item in slip) printItem(item)

        // print thank you message at end of slip
        val thankYouMsg = PrintObject.Data("Thanks for using PAX POS", PrintStringConfiguration(displayCenter = true))
        printItem(thankYouMsg)

        // print users copy
        val userCopy = PrintObject.Data("*** $user copy ***".toUpperCase(), PrintStringConfiguration(displayCenter = true))
        printItem(userCopy)

        return try {
            printer.printString()
            // set step distance
            printer.walkPaper(80)
            PrintStatus.Ok("Printed")
        } catch (exception: TelpoException) {
            PrintStatus.Ok("Failed to print: ${exception.localizedMessage}")
        }
    }

    override fun canPrint(): PrintStatus {
        return when (printer.checkStatus()) {
            UsbThermalPrinter.STATUS_NO_PAPER -> PrintStatus.Error("No paper in the tray")
            UsbThermalPrinter.STATUS_OVER_FLOW -> PrintStatus.Error("Overflow error")
            UsbThermalPrinter.STATUS_OVER_HEAT -> PrintStatus.Error("Device overheating")
            UsbThermalPrinter.STATUS_UNKNOWN -> PrintStatus.Error("Unknown error")
            else -> PrintStatus.Ok("Can print!")
        }
    }

    private fun printItem(item: PrintObject) {

        when (item) {
            is PrintObject.Line -> printer.addString(line)
            is PrintObject.BitMap -> printer.printLogo(item.bitmap, true)
            is PrintObject.Data -> {
                // get string print config
                val printConfig = item.config

                // set font size
                when {
                    printConfig.isTitle -> {
                        printer.setGray(7)
                        printer.setFontSize(36)
                    }
                    else -> {
                        printer.setFontSize(24)
                    }
                }

                // set gray thickness
//                if (printConfig.isBold || printConfig.isTitle) printer.setGray(4)
//                else printer.setGray(2)

                // print string
                if (printConfig.displayCenter) {
                    printer.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                    printer.addString(item.value)
                } else {
                    printer.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                    printer.addString(item.value)
                }
            }

        }
    }

    companion object {
        private const val SCREEN_LARGE_LENGTH = 24
        private const val SCREEN_NORMAL_LENGTH = 32
    }
}