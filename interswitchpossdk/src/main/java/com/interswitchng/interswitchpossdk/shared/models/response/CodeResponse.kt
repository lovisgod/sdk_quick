package com.interswitchng.interswitchpossdk.shared.models.response

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils

data class CodeResponse(
        val responseCode: String,
        val responseDescription: String,
        val date: String,
        val reference: String,
        val bankShortCode: String,
        val defaultShortCode: String,
        val terminalId: String,
        val transactionReference: String,
        val qrCodeData: String?
) {


    @Throws(WriterException::class)
    internal fun getBitmap(context: Context, lengthInDp: Int = 300): Bitmap? {
        return qrCodeData?.let {

            try {
                // length in pixels
                val length = DisplayUtils.convertDpToPixel(lengthInDp.toFloat(), context).toInt()

                // create bitmap matrix
                val bitMatrix: BitMatrix = MultiFormatWriter().encode(it, BarcodeFormat.QR_CODE, length, length, null)

                // get matrix dimensions
                val bitMatrixWidth = bitMatrix.width
                val bitMatrixHeight = bitMatrix.height

                // create array with area of matrix
                val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

                // generate the coloring of bitmap with area
                for (y in 0 until bitMatrixHeight) {
                    val offset = y * bitMatrixWidth

                    for (x in 0 until bitMatrixWidth) {
                        val color =
                                if (bitMatrix.get(x, y)) android.R.color.black
                                else android.R.color.white

                        // set the color of specific pixel
                        pixels[offset + x] = ContextCompat.getColor(context, color)
                    }
                }

                // create and return bitmap
                val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
                bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight)
                return bitmap

            } catch (e: IllegalArgumentException) {
                return null
            } catch (ex: Exception) {
                ex.printStackTrace()
                return null
            }
        }
    }
}
