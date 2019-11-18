package com.interswitchng.smartpos.modules.setup

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint

class SetupFragmentViewModel constructor(
    private val posFingerprint: POSFingerprint
) : ViewModel() {

    private val _fingerPrintBitmap = MutableLiveData<Bitmap>()
    val fingerPrintBitmap: LiveData<Bitmap>
        get() = _fingerPrintBitmap

    fun enrollMerchantCard() {

    }

    fun enrollMerchantFingerPrint() {
        posFingerprint.enrollFinger {
            _fingerPrintBitmap.postValue(it)
        }
    }

    fun removeMerchantFingerPrint() {
        _fingerPrintBitmap.postValue(null)
        posFingerprint.removeFinger()
    }

    fun setupServer() {

    }
}