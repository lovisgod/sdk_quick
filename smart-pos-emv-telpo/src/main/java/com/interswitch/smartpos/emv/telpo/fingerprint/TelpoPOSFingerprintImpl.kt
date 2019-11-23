package com.interswitch.smartpos.emv.telpo.fingerprint

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.interswitch.smartpos.emv.telpo.utils.RawToBitMap
import com.interswitch.smartpos.emv.telpo.utils.TelpoFingerPrintConstants
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.utilities.FileUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.telpo.usb.finger.Finger

class TelpoPOSFingerprintImpl : POSFingerprint {

    private val logger by lazy { Logger.with("TelpoPOSFingerprintImpl") }

    private var fingerPrintImageData = ByteArray(250 * 360)
    private val fingerPrintISO1 = ByteArray(890)
    private val fingerPrintISO2 = ByteArray(890)

    private lateinit var fingerPrintISO11: ByteArray
    private lateinit var fingerPrintISO22: ByteArray
    var ret = 0

    var errorMessage = ""

    init {
        val byte = ByteArray (250 * 360)
        Finger.initialize(byte)
    }

    override fun createFinger(context: Context, phoneNumber: String): Boolean {
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
        val result = FileUtils(context).saveMerchantFingerPrint(phoneNumber, fingerprintISO)
        if (!result.first) {
            errorMessage = "Could not save fingerprint"
            return false
        }

        return true
    }

    override fun enrollFinger(
        context: Context,
        phoneNumber: String,
        onComplete: (Pair<String?, Bitmap?>) -> Unit
    ) {
        ret = Finger.get_image(fingerPrintImageData)
        if (ret == TelpoFingerPrintConstants.SUCCESS) {
            val bitmap = RawToBitMap.convert8bit(fingerPrintImageData, 208, 288)
            val length = IntArray(1)
            val quality = ByteArray(1)
            ret = Finger.get_ISO2005(fingerPrintISO1, length, quality)

            fingerPrintISO11 = ByteArray(length[0])
            System.arraycopy(fingerPrintISO1, 0, fingerPrintISO11, 0, length[0])

            if (bitmap != null) {
                val result = FileUtils(context).saveMerchantFingerPrint(phoneNumber, fingerPrintISO1)
                if (result.first) {
                    onComplete.invoke(Pair(null, bitmap))
                } else {
                    onComplete.invoke(Pair(result.second, null))
                }
            }
        } else onComplete.invoke(Pair("Could not capture fingerprint", null))
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
}