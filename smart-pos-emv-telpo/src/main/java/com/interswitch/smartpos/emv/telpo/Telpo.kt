package com.interswitch.smartpos.emv.telpo

import android.content.Context
import com.telpo.tps550.api.reader.SmartCardReader

class Telpo constructor(
    private val context: Context
) {

    private var isReadingCard = true
    private val startTime = System.currentTimeMillis()

    fun startCardReading() {
        val reader = SmartCardReader(context)
        while (isReadingCard) {
            if ((System.currentTimeMillis() - startTime) >= 10000) {
                isReadingCard = false
                return
            }
            isReadingCard = !reader.open()
        }
    }
}