package com.interswitchng.smartpos.modules.main.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.interswitchng.smartpos.modules.main.models.RegisteredFingerprint
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore

internal class FingerprintViewModel (
    private val fingerprint: POSFingerprint,
    private val keyValueStore: KeyValueStore
) : ViewModel() {

    private val gson = Gson()

    private val phoneNumber by lazy { keyValueStore.getString("M3RCHANT_PHONE", "") }

    private val _registeredFingerprints = MutableLiveData<List<RegisteredFingerprint>>()
    val registeredFingerprint: LiveData<List<RegisteredFingerprint>>
        get() = _registeredFingerprints

    fun getFingerprints(context: Context, phoneNumber: String) {
        if (fingerprint.hasFingerprint(context, phoneNumber)) {
            _registeredFingerprints.postValue(listOf(
                RegisteredFingerprint(
                    userName = "User 1",
                    userId = "567iiuyueisda"
                )
            ))
        } else _registeredFingerprints.postValue(emptyList())
    }

    fun hasFingerprint(context: Context) = fingerprint.hasFingerprint(context, phoneNumber)

    fun confirmFinger(context: Context) = fingerprint.confirmFinger(context, phoneNumber)

    fun createFingerprint(context: Context): Boolean {
        val startTime = System.currentTimeMillis()
        while (true) {
            if (fingerprint.createFinger(context, phoneNumber)) break
            if ((System.currentTimeMillis() - startTime) > 10000) break
        }
        return false
    }
}