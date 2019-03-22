package com.interswitchng.smartpos.emv.pax.services


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import com.interswitchng.smartpos.emv.pax.utilities.StringUtils
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.PrintStatus
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode

object DevicePrinterImpl : DevicePrinter {

    // screen caharacter length
    private const val SCREEN_LARGE_LENGTH = 24
    private const val SCREEN_NORMAL_LENGTH = 32

    // font sizes
    private val NORMAL_FONT = Pair(EFontTypeAscii.FONT_16_24, EFontTypeExtCode.FONT_16_16)
    private val LARGE_FONT = Pair(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_32)

    private val line: String = "-".repeat(SCREEN_NORMAL_LENGTH)

    override fun canPrint(): PrintStatus {
        val result = PaxPrinter.getInstance().status

        // return result
        return if (result.first == PaxPrinter.PRINT_STATUS_OK) PrintStatus.Ok(result.second)
        else PrintStatus.Error(result.second)
    }

    override fun printSlip(slip: List<PrintObject>, user: UserType): PrintStatus {
        val printer = PaxPrinter.getInstance()

        // initialize printer
        printer.init()

        // set line spacing
        printer.spaceSet(0, 10)

        // set step distance
        printer.step(60)

        // print logo
        printCompanyLogo(printer)

        // print 2 new lines for distance from logo
        printer.fontSet(NORMAL_FONT.first, NORMAL_FONT.second)
        printer.printStr("\n", null)

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
        val result = printer.start()

        // return result
        return if (result.first == PaxPrinter.PRINT_STATUS_OK) PrintStatus.Ok(result.second)
        else PrintStatus.Error(result.second)
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


    fun printCompanyLogo(printer: PaxPrinter) {
        POSDeviceImpl.companyLogo.also { logo ->
            // copy out bitmap
            val it = logo.copy(logo.config, logo.isMutable)
            val smallScale =
                    if (it.width == it.height) getScaledDownBitmap(it)
                    else getScaledDownBitmap(it, threshold = 200)
            val paddingLeft = ((SCREEN_NORMAL_LENGTH * 12.5) - smallScale.width) / 2 // 1 dot in print is 12.5px

            // add padding to bitmap
            val outputBitmap = Bitmap.createBitmap(smallScale.width + paddingLeft.toInt(), smallScale.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            // draw logo in output bitmap
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(smallScale, paddingLeft.toFloat(), 0f, null)

            // print bitmap
            printer.printBitmap(outputBitmap)
        }
    }


    /**
     * @param bitmap the Bitmap to be scaled
     * @param threshold the maxium dimension (either width or height) of the scaled bitmap
     * @param isNecessaryToKeepOrig is it necessary to keep the original bitmap? If not recycle the original bitmap to prevent memory leak.
     */

    fun getScaledDownBitmap(bitmap: Bitmap, threshold: Int = 150, isNecessaryToKeepOrig: Boolean = false): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        var newWidth = width
        var newHeight = height

        if (width > height && width > threshold) {
            newWidth = threshold
            newHeight = (height * newWidth.toFloat() / width).toInt()
        }

        if (width in (height + 1)..threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap
        }

        if (width < height && height > threshold) {
            newHeight = threshold
            newWidth = (width * newHeight.toFloat() / height).toInt()
        }

        if (height in (width + 1)..threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap
        }

        if (width == height && width > threshold) {
            newWidth = threshold
            newHeight = newWidth
        }

        return if (width == height && width <= threshold) {
            //the bitmap is already smaller than our required dimension, no need to resize it
            bitmap
        } else getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig)

    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int, isNecessaryToKeepOrig: Boolean): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        if (!isNecessaryToKeepOrig) {
            bm.recycle()
        }
        return resizedBitmap
    }
}