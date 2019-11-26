package com.interswitchng.smartpos.modules.main.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.interswitchng.smartpos.modules.main.models.RegisteredFingerprint
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.fingerprint.Fingerprint
import com.interswitchng.smartpos.shared.viewmodel.RootViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

internal class FingerprintViewModel (
    private val fingerprint: POSFingerprint,
    private val keyValueStore: KeyValueStore
) : RootViewModel(TAG) {

    companion object {
        const val TAG = "FingerprintViewModel"
    }

    private val channel = Channel<Fingerprint>()

    private val gson = Gson()

    private val phoneNumber by lazy { keyValueStore.getString("M3RCHANT_PHONE", "") }
    private val merchantPAN by lazy { keyValueStore.getString("M3RCHANT_PAN", "") }

    private val _fingerPrintResult = MutableLiveData<Fingerprint>()
    val fingerPrintResult: LiveData<Fingerprint>
        get() = _fingerPrintResult

    private val _registeredFingerprints = MutableLiveData<List<RegisteredFingerprint>>()
    val registeredFingerprint: LiveData<List<RegisteredFingerprint>>
        get() = _registeredFingerprints

    fun setup(context: Context) {

    }

    fun getFingerprints() {
//        if (fingerprint.hasFingerprint(context, phoneNumber)) {
//            _registeredFingerprints.postValue(listOf(
//                RegisteredFingerprint(
//                    userName = "User 1",
//                    userId = phoneNumber
//                )
//            ))
//        } else _registeredFingerprints.postValue(emptyList())
    }

    fun hasFingerprint(context: Context) = fingerprint.hasFingerprint(context, phoneNumber)

    fun confirmFinger(context: Context) = fingerprint.confirmFinger(context, phoneNumber)

    fun createFingerprint(context: Context) {
        with (uiScope) {
            launch (ioScope) {
                for (result in channel) {
                    if (result is Fingerprint.SetupComplete) {
                        launch (ioScope) {
                            fingerprint.createFinger()
                        }
                    }
                    logger.logErr("This is the result === $result")
                    _fingerPrintResult.postValue(result)
                }
            }

            launch (ioScope) {
                fingerprint.setup(context, phoneNumber, channel)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fingerprint.close()
    }
}