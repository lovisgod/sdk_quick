package com.interswitchng.smartpos.shared.utilities

import android.content.Context
import java.io.*

class FileUtils constructor(
    private val context: Context
) {
    private val rootFolder by lazy {
        File(context.cacheDir, "fingerprint").apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    fun saveMerchantFingerPrint(
        phoneNumber: String,
        byteArray: ByteArray
    ): Pair<Boolean, String> {
        val newFile = File(rootFolder, "FingerPrint_$phoneNumber")
        if (newFile.exists()) {
            return Pair(false, "This Merchant has already been enrolled!")
        }
        val outPutStream = FileOutputStream(newFile)
        return try {
            outPutStream.write(byteArray)
            Pair(true, "Merchant successfully enrolled")
        } catch (exception: FileNotFoundException) {
            Pair(false, "Could not enroll. Please try again.")
        } catch (exception: IOException) {
            Pair(false, "An exception occurred while enrolling merchant. Please try again.")
        } finally {
            outPutStream.close()
        }
    }

    fun getFingerPrintFile(phoneNumber: String): ByteArray {
        val file = File(rootFolder, "FingerPrint_$phoneNumber")
        return if (!file.exists()) byteArrayOf() else readBinaryFile(file.absolutePath)
    }

    private fun readBinaryFile(inputFileName: String?): ByteArray {
        val file = File(inputFileName)
        val result = ByteArray(file.length().toInt())
        try {
            var input: InputStream? = null
            try {
                var totalBytesRead = 0
                input = BufferedInputStream(FileInputStream(file))
                while (totalBytesRead < result.size) {
                    val bytesRemaining = result.size - totalBytesRead

                    val bytesRead = input.read(result, totalBytesRead, bytesRemaining)
                    if (bytesRead > 0) {
                        totalBytesRead += bytesRead
                    }
                }
            } finally {
                input!!.close()
            }
        } catch (ex: FileNotFoundException) {
        } catch (ex: IOException) {
        }
        return result
    }
}