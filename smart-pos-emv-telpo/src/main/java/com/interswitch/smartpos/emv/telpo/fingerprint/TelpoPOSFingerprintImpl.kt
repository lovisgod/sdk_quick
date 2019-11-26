package com.interswitch.smartpos.emv.telpo.fingerprint

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.interswitch.smartpos.emv.telpo.utils.RawToBitMap
import com.interswitch.smartpos.emv.telpo.utils.TelpoFingerPrintConstants
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.models.fingerprint.Fingerprint
import com.interswitchng.smartpos.shared.utilities.FileUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.usb.finger.Finger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class TelpoPOSFingerprintImpl : POSFingerprint {

    private val logger by lazy { Logger.with("TelpoPOSFingerprintImpl") }

    private lateinit var channel: Channel<Fingerprint>
    private lateinit var context: Context

    private lateinit var fingerPrintData: ByteArray
    private lateinit var fingerPrintISO: ByteArray

    private lateinit var phoneNumber: String

    private lateinit var bitmap: Bitmap

    private lateinit var fingerPrintISO11: ByteArray
    private lateinit var fingerPrintISO22: ByteArray
    var ret = 0

    var errorMessage = ""
    private var isCancelled = false

    init {
        val byte = ByteArray (250 * 360)
        Finger.initialize(byte)
    }

    override suspend fun setup(
        context: Context,
        phoneNumber: String,
        channel: Channel<Fingerprint>
    ) {
        this.context = context
        this.channel = channel
        fingerPrintData = ByteArray(250 * 360)
        fingerPrintISO = ByteArray(890)
        this.phoneNumber = phoneNumber
    }

    override suspend fun createFinger() {
        while (coroutineContext.isActive && !isCancelled) {
            val result = checkFingerprint()
            logger.logErr("Here")
            if (result) break
        }
        logger.logErr("Here 1")
        channel.send(Fingerprint.Detected(bitmap))
        logger.logErr("Here 2")

        val result = FileUtils(context).saveMerchantFingerPrint(phoneNumber, fingerPrintISO, channel)
        logger.logErr("Write file result == $result")
//        if (!result.first) {
//            channel.send(Fingerprint.Failed(result.second))
//            isCancelled = true
//            return
//        }

        //This delay is for the UI to change before the dialog dismisses
        delay(1000)

        channel.send(Fingerprint.Success)
    }

    private fun checkFingerprint(): Boolean {
        fingerPrintData = ByteArray(250 * 360)
        fingerPrintISO = ByteArray(890)

        val length = IntArray(1)
        val quality = ByteArray(1)

        ret =  Finger.get_image(fingerPrintData)
        if (ret != 0) return false
        bitmap = RawToBitMap.convert8bit(fingerPrintData, 208, 288) ?: return false
        ret = Finger.get_ISO2005(fingerPrintISO, length, quality)

        return ret == 0
    }

    private fun getFingerprint(context: Context, phoneNumber: String): Boolean {
        errorMessage = ""
        val data = ByteArray(250 * 360)
        val fingerprintISO = ByteArray(890)
        ret = Finger.get_image(data)
        logger.log("Get fingerprint data: RET = $ret")
        if (ret != 0) {
            errorMessage = "Could not get fingerprint data"
            return false
        }

        val length = IntArray(1)
        val quality = ByteArray(1)

        ret = Finger.get_ISO2005(fingerprintISO, length, quality)
        logger.log("Get fingerprint ISO: RET = $ret")

        if (ret != 0) {
            errorMessage = "Could not get fingerprint ISO"
            return false
        }
//        val result = FileUtils(context).saveMerchantFingerPrint(phoneNumber, fingerprintISO)
//        if (!result.first) {
//            errorMessage = "Could not save fingerprint"
//            return false
//        }

        return true
    }

    override fun enrollFinger(
        context: Context,
        phoneNumber: String,
        onComplete: (Pair<String?, Bitmap?>) -> Unit
    ) {
        ret = Finger.get_image(fingerPrintData)
        if (ret == TelpoFingerPrintConstants.SUCCESS) {
            val bitmap = RawToBitMap.convert8bit(fingerPrintData, 208, 288)
            val length = IntArray(1)
            val quality = ByteArray(1)
            ret = Finger.get_ISO2005(fingerPrintISO, length, quality)

            fingerPrintISO11 = ByteArray(length[0])
            System.arraycopy(fingerPrintISO, 0, fingerPrintISO11, 0, length[0])

//            if (bitmap != null) {
//                val result = FileUtils(context).saveMerchantFingerPrint(phoneNumber, fingerPrintISO)
//                if (result.first) {
//                    onComplete.invoke(Pair(null, bitmap))
//                } else {
//                    onComplete.invoke(Pair(result.second, null))
//                }
            }
//        } else onComplete.invoke(Pair("Could not capture fingerprint", null))
    }

    override fun removeFinger() {}

    override fun confirmFinger(context: Context, phoneNumber: String): Boolean {
        val savedFingerPrintISO = FileUtils(context).getFingerPrintFile(phoneNumber)
        Log.e("TelpoPOSFingerprintImpl", "Saved ISO is empty == ${savedFingerPrintISO.isEmpty()}")
        if (savedFingerPrintISO.isEmpty()) return false

        val capturedFingerPrintImageData = ByteArray(250 * 360)
        Finger.get_image(capturedFingerPrintImageData)
        val capturedFingerPrintISO = ByteArray(890)

        val length = IntArray(1)
        val quality = ByteArray(1)

        Finger.get_ISO2005(capturedFingerPrintISO, length, quality)

        val result = IntArray(1)

        return Finger.verify_ISO_tmpl(savedFingerPrintISO, savedFingerPrintISO, result) == 0
    }

    override fun hasFingerprint(context: Context, phoneNumber: String): Boolean {
        return FileUtils(context).getFingerPrintFile(phoneNumber).isNotEmpty()
    }

    override fun close() {
        isCancelled = true
        Finger.terminate()
    }
}