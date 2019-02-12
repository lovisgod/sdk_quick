package com.igweze.ebi.paxemvcontact.services

import com.igweze.ebi.paxemvcontact.posshim.Printer
import com.igweze.ebi.paxemvcontact.utilities.StringUtils
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.models.core.UserType
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintStringConfiguration
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode

object Printer: IPrinter {

    // screen caharacter length
    private const val SCREEN_LARGE_LENGTH = 24
    private const val SCREEN_NORMAL_LENGTH = 32

    // font sizes
    private val NORMAL_FONT = Pair(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16)
    private val LARGE_FONT = Pair(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_32)

    private val line: String = "-".repeat(SCREEN_LARGE_LENGTH)

    //----------------------------------------------------------
    //      Implementation for ISW Printer interface
    //----------------------------------------------------------
    override fun printSlip(slip: List<PrintObject>, user: UserType) {
        Thread {
            val printer = Printer.getInstance()

            // initialize printer
            printer.init()

            // set line spacing
            printer.spaceSet(0, 10)

            // set step distance
            printer.step(60)

            // extract slip items and print it
            for (item in slip) printItem(printer, item)

            // print thank you message at end of slip
            val thankYouMsg = PrintObject.Data("Thanks for using PAX POS", PrintStringConfiguration(displayCenter = true))
            printItem(printer, thankYouMsg)

            // print users copy
            val userCopy = PrintObject.Data("*** $user copy ***".toUpperCase(), PrintStringConfiguration(displayCenter = true))
            printItem(printer, userCopy)

            // set step distance
            printer.step(80)

            // start printing
            printer.start()
        }.start()
    }

    private fun printItem(printer: Printer, item: PrintObject) {

        when (item) {
            is PrintObject.Line -> printer.printStr(line, null)
            is PrintObject.BitMap -> printer.printBitmap(item.bitmap)
            is PrintObject.Data -> {
                // get string print config
                val printConfig = item.config

                // set font size
                val fontSize = when {
                    printConfig.isTitle -> LARGE_FONT
                    else -> NORMAL_FONT
                }

                printer.fontSet(fontSize.first, fontSize.second)

                // set gray thickness
                if (printConfig.isBold) printer.setGray(4)
                else printer.setGray(1)

                // print string
                if (printConfig.displayCenter) {
                    val screenLength = when (fontSize) {
                        LARGE_FONT -> SCREEN_LARGE_LENGTH
                        else -> SCREEN_NORMAL_LENGTH
                    }

                    val formattedString = StringUtils.center(item.value, screenLength, newLine = true)

                    printer.printStr(formattedString, null)
                } else {
                    printer.printStr(item.value, null)
                }
            }

        }
    }

}