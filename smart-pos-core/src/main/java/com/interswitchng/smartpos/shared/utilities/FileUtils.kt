package com.interswitchng.smartpos.shared.utilities

import android.content.Context
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.EmvAIDs
import com.interswitchng.smartpos.shared.models.posconfig.TerminalConfig
import org.simpleframework.xml.core.Persister
import java.io.*
//
//class FileUtils constructor(
//    private val context: Context
//) {
//    private val rootFolder by lazy {
//        File(context.cacheDir, "fingerprint").apply {
//            if (!exists()) {
//                mkdir()
//            }
//        }
//    }
//
//    fun saveMerchantFingerPrint(
//        phoneNumber: String,
//        byteArray: ByteArray
//    ): Pair<Boolean, String> {
//        val newFile = File(rootFolder, "FingerPrint_$phoneNumber")
//        if (newFile.exists()) {
//            return Pair(false, "This Merchant has already been enrolled!")
//        }
//        val outPutStream = FileOutputStream(newFile)
//        return try {
//            outPutStream.write(byteArray)
//            Pair(true, "Merchant successfully enrolled")
//        } catch (exception: FileNotFoundException) {
//            Pair(false, "Could not enroll. Please try again.")
//        } catch (exception: IOException) {
//            Pair(false, "An exception occurred while enrolling merchant. Please try again.")
//        } finally {
//            outPutStream.close()
//        }
//    }
//
//    fun getFingerPrintFile(phoneNumber: String): ByteArray {
//        val file = File(rootFolder, "FingerPrint_$phoneNumber")
//        return if (!file.exists()) byteArrayOf() else readBinaryFile(file.absolutePath)
//    }
//
//    private fun readBinaryFile(inputFileName: String?): ByteArray {
//        val file = File(inputFileName)
//        val result = ByteArray(file.length().toInt())
//        try {
//            var input: InputStream? = null
//            try {
//                var totalBytesRead = 0
//                input = BufferedInputStream(FileInputStream(file))
//                while (totalBytesRead < result.size) {
//                    val bytesRemaining = result.size - totalBytesRead
//
//                    val bytesRead = input.read(result, totalBytesRead, bytesRemaining)
//                    if (bytesRead > 0) {
//                        totalBytesRead += bytesRead
//                    }
//                }
//            } finally {
//                input!!.close()
//            }
//        } catch (ex: FileNotFoundException) {
//        } catch (ex: IOException) {
//        }
//        return result
//    }
//}



class FileUtils  constructor(
    private val context: Context
) {


    private val rootFolder by lazy {
        File(context.cacheDir, "fingerprint").apply {
            if (!exists()) {
                mkdir()
            }
        }
    }

    internal fun <T> readXml(clazz: Class<T>, xmlFile: InputStream): T {
        val deserializer = Persister()
        return deserializer.read(clazz, xmlFile)
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
    internal fun getFilesArrayFromAssets(context: Context, path: String): Array<String>? {
        val assetManager = context.resources.assets
        var files: Array<String>? = null
        try {
            files = assetManager.list(path);
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if(files != null) {
            for (i in 0 until files.size) {
                files[i] = path + "/" + files[i]
            }
        }

        return files;
    }


    internal fun getFromAssets(context: Context): ByteArray? {
        var ins: InputStream? = null
        try {
            ins = context.resources.assets.open("jpos.xml");

            val length = ins.available()
            val buffer = ByteArray(length)
            ins.read(buffer)
            return buffer
        } catch (e: Exception) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    internal fun getAids(xmlFile: InputStream): EmvAIDs = readXml(EmvAIDs::class.java, xmlFile)
    internal fun getTerminalConfig(xmlFile: InputStream): TerminalConfig = readXml(TerminalConfig::class.java, xmlFile)


    fun getConfigurations(context: Context, terminalInfo: TerminalInfo): Pair<TerminalConfig, EmvAIDs> {
        // get resource streams
        val emvStr = context.resources.openRawResource(R.raw.isw_emv_config)
        val terminalStr = context.resources.openRawResource(R.raw.isw_terminal_config)
        // extract values
        val emvApps = getAids(emvStr)
        val terminalConfig = getTerminalConfig(terminalStr)
        // reset terminal capabilities if configured on the terminal
        terminalConfig.terminalcapability = terminalInfo.capabilities ?: terminalConfig.terminalcapability

        return Pair(terminalConfig, emvApps)
    }
}
