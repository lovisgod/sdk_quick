package com.interswitch.smartpos.emv.telpo

import android.content.Context
import com.telpo.tps550.api.reader.SmartCardReader

class Telpo constructor(
    private val context: Context
) {

    fun startCardReading() {
        val reader = SmartCardReader(context)
    }
}