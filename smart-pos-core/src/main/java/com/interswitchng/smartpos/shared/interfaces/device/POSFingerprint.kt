package com.interswitchng.smartpos.shared.interfaces.device

import android.content.Context
import android.graphics.Bitmap
import com.interswitchng.smartpos.shared.models.fingerprint.FingerprintResult
import kotlinx.coroutines.channels.Channel

interface POSFingerprint {

    fun enrollFinger(
        context: Context,
        phoneNumber: String,
        onComplete: ((Pair<String?, Bitmap?>)) -> Unit
    )

    suspend fun createFinger(
        context: Context,
        phoneNumber: String,
        channel: Channel<FingerprintResult>
    )

    fun removeFinger()

    fun confirmFinger(context: Context, phoneNumber: String): Boolean

    fun hasFingerprint(context: Context, phoneNumber: String): Boolean

    fun close()
}