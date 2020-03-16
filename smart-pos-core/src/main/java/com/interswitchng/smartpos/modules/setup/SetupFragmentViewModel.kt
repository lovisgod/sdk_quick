package com.interswitchng.smartpos.modules.setup

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

internal class SetupFragmentViewModel constructor(
        private val posDevice: POSDevice,
        private val keyValueStore: KeyValueStore
) : ViewModel(), KoinComponent {

    private val posFingerprint: POSFingerprint
        get() {
            if (posDevice.hasFingerPrintReader) {
                return get()
            } else {
                throw Exception("Finger Print is not supported for this device")
            }
        }

    private val _fingerPrintBitmap = MutableLiveData<Bitmap>()
    val fingerPrintBitmap: LiveData<Bitmap>
        get() = _fingerPrintBitmap

    fun isSetup() = keyValueStore.getBoolean("SETUP")

    fun saveMerchantPAN(pan: String) {
        keyValueStore.saveString("M3RCHANT_PAN", pan)
    }

    fun saveMerchantPhoneNumber(number: String) {
        keyValueStore.saveString("M3RCHANT_PHONE", number)
        keyValueStore.saveBoolean("SETUP", true)
    }

    fun enrollMerchantCard() {

    }

    fun removeMerchantFingerPrint() {
        if (posDevice.hasFingerPrintReader) {
            _fingerPrintBitmap.postValue(null)
            posFingerprint.removeFinger()
        }
    }

    fun setupServer() {

    }
}