package com.interswitchng.smartpos.modules.setup

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore

internal class SetupFragmentViewModel constructor(
    private val posFingerprint: POSFingerprint,
    private val keyValueStore: KeyValueStore
) : ViewModel() {

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

    fun enrollMerchantFingerPrint(context: Context, phoneNumber: String) {
        posFingerprint.enrollFinger(context, phoneNumber) {
            _fingerPrintBitmap.postValue(it.second)
        }
    }

    fun removeMerchantFingerPrint() {
        _fingerPrintBitmap.postValue(null)
        posFingerprint.removeFinger()
    }

    fun verify(context: Context, phoneNumber: String): Boolean {
        return posFingerprint.confirmFinger(context, phoneNumber)
    }

    fun setupServer() {

    }
}