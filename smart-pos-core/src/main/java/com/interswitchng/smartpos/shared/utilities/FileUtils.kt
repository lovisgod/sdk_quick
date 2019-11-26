package com.interswitchng.smartpos.shared.utilities

import android.content.Context
import com.interswitchng.smartpos.shared.models.fingerprint.Fingerprint
import kotlinx.coroutines.channels.Channel
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

    suspend fun saveMerchantFingerPrint(
        phoneNumber: String,
        byteArray: ByteArray,
        channel: Channel<Fingerprint>
    ) {
        val newFile = File(rootFolder, "FingerPrint_$phoneNumber")
        if (newFile.exists()) {
            channel.send(Fingerprint.Failed("Fingerprint Exists"))
        }
        val outPutStream = FileOutputStream(newFile)
        return try {
            outPutStream.write(byteArray)
            channel.send(Fingerprint.WriteSuccessful)
        } catch (exception: FileNotFoundException) {
            channel.send(Fingerprint.Failed("Could not enroll. Please try again."))
        } catch (exception: IOException) {
            channel.send(Fingerprint.Failed("An exception occurred while enrolling merchant. Please try again."))
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