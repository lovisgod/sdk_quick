package com.interswitch.smartpos.emv.telpo.fingerprint

import android.graphics.Bitmap
import android.util.Log
import com.interswitch.smartpos.emv.telpo.utils.FileUtils
import com.interswitch.smartpos.emv.telpo.utils.RawToBitMap
import com.interswitch.smartpos.emv.telpo.utils.TelpoFingerPrintConstants
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.telpo.usb.finger.Finger

class TelpoPOSFingerprintImpl : POSFingerprint {

    private var fingerPrintImageData = ByteArray(250 * 360)
    private val fingerPrintISO1 = ByteArray(890)
    private val fingerPrintISO2 = ByteArray(890)

    private lateinit var fingerPrintISO11: ByteArray
    private lateinit var fingerPrintISO22: ByteArray
    var ret = 0

    init {
        val byte = ByteArray (250 * 360)
        Finger.initialize(byte)
    }

    override fun enrollFinger(onComplete: (Bitmap) -> Unit) {
        ret = Finger.get_image(fingerPrintImageData)
        if (ret == TelpoFingerPrintConstants.SUCCESS) {
            val bitmap = RawToBitMap.convert8bit(fingerPrintImageData, 208, 288)
            val length = IntArray(1)
            val quality = ByteArray(1)
            ret = Finger.get_ISO2005(fingerPrintISO1, length, quality)

            Log.e("Fingerprint Byte", fingerPrintImageData.toString())

            fingerPrintISO11 = ByteArray(length[0])
            System.arraycopy(fingerPrintISO1, 0, fingerPrintISO11, 0, length[0])
            FileUtils.createFileWithByte(fingerPrintISO11, "ISO11")
            if (bitmap != null) {
                onComplete.invoke(bitmap)
            }
        }
    }

    override fun removeFinger() {
        fingerPrintImageData = ByteArray(250 * 360)
        Finger.clear_alltemplate()
    }

    override fun confirmFinger() {

    }
}