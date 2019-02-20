package com.interswitchng.smartpos.emv.pax.services


import com.interswitchng.smartpos.emv.pax.utilities.StringUtils
import com.interswitchng.smartpos.shared.interfaces.device.IPrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode

object Printer: IPrinter {

    // screen caharacter length
    private const val SCREEN_LARGE_LENGTH = 24
    private const val SCREEN_NORMAL_LENGTH = 32

    // font sizes
    private val NORMAL_FONT = Pair(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16)
    private val LARGE_FONT = Pair(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_32)

    private val line: String = "-".repeat(SCREEN_NORMAL_LENGTH)

    //----------------------------------------------------------
    //      Implementation for ISW Printer interface
    //----------------------------------------------------------
    override fun printSlip(slip: List<PrintObject>, user: UserType) {
        Thread {
            val printer = PaxPrinter.getInstance()

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

    private fun printItem(paxPrinter: PaxPrinter, item: PrintObject) {

        when (item) {
            is PrintObject.Line -> paxPrinter.printStr(line, null)
            is PrintObject.BitMap -> paxPrinter.printBitmap(item.bitmap)
            is PrintObject.Data -> {
                // get string print config
                val printConfig = item.config

                // set font size
                val fontSize = when {
                    printConfig.isTitle -> LARGE_FONT
                    else -> NORMAL_FONT
                }

                paxPrinter.fontSet(fontSize.first, fontSize.second)

                // set gray thickness
                if (printConfig.isBold || printConfig.isTitle) paxPrinter.setGray(4)
                else paxPrinter.setGray(2)

                // print string
                if (printConfig.displayCenter) {
                    val screenLength = when (fontSize) {
                        LARGE_FONT -> SCREEN_LARGE_LENGTH
                        else -> SCREEN_NORMAL_LENGTH
                    }

                    val formattedString = StringUtils.center(item.value, screenLength, newLine = true)

                    paxPrinter.printStr(formattedString, null)
                } else {
                    paxPrinter.printStr(item.value, null)
                }
            }

        }
    }

}