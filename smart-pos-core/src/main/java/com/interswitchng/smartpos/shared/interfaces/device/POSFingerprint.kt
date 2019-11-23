package com.interswitchng.smartpos.shared.interfaces.device

import android.content.Context
import android.graphics.Bitmap

interface POSFingerprint {

    fun enrollFinger(
        context: Context,
        phoneNumber: String,
        onComplete: ((Pair<String?, Bitmap?>)) -> Unit
    )

    fun createFinger(
        context: Context,
        phoneNumber: String
    ): Boolean

    fun removeFinger()

    fun confirmFinger(context: Context, phoneNumber: String): Boolean

    fun hasFingerprint(context: Context, phoneNumber: String): Boolean
}