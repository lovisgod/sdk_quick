package com.igweze.ebi.paxemvcontact

import com.igweze.ebi.paxemvcontact.posshim.*
import com.igweze.ebi.paxemvcontact.utilities.StringUtils
import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.interfaces.CardInsertedCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintStringConfiguration
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode
import java.util.concurrent.Executors

class POSDeviceService(private val device: PosInterface) : POSDevice, CardService.Callback {

    private val line: String = "-".repeat(SCREEN_NORMAL_LENGTH)

    private var cardCallback: CardInsertedCallback? = null


    //----------------------------------------------------------
    //      Implementation for ISW POSDevice interface
    //----------------------------------------------------------
    override fun attachCallback(callback: CardInsertedCallback) {
        cardCallback = callback
        // attach callback to device
        device.attachCallback(this)
    }

    override fun detachCallback(callback: CardInsertedCallback) {
        device.removeCallback(this)
        cardCallback = null
    }

    override fun printSlip(slip: List<PrintObject>, user: String) {
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

    override fun checkPin(string: String) {
        device.checkPin(string)
    }


    //---------------------------------------------------------------
    // Implementation for NeptuneSDK CardService.Callback interface
    //----------------------------------------------------------------
    override fun onCardDetected() {
        cardCallback?.onCardDetected()
    }

    override fun onCardRead(card: Card?) {
        card?.apply {
            val cardCopy = CardDetail(pan, expiry)
            cardCallback?.onCardRead(cardCopy)
        }
    }

    override fun onCardRemoved() {
        cardCallback?.onCardRemoved()
    }

    override fun onError(error: PosError?) {
        error?.apply {
            // TODO get the correct error code from POSError
            val errorCode = DeviceError.ERR_CARD_ERROR
            // translate error to domain error
            val deviceError = DeviceError(errorMessage, errorCode)
            cardCallback?.onError(deviceError)
        }
    }

    companion object {
        const val SCREEN_LARGE_LENGTH = 24
        const val SCREEN_NORMAL_LENGTH = 32

        val NORMAL_FONT = Pair(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16)
        val LARGE_FONT = Pair(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_32)

    }
}