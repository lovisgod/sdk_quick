package com.interswitchng.smartpos.shared.interfaces.device

import android.graphics.Bitmap

interface POSFingerprint {

    fun enrollFinger(onComplete: (Bitmap) -> Unit)

    fun removeFinger()

    fun confirmFinger()
}