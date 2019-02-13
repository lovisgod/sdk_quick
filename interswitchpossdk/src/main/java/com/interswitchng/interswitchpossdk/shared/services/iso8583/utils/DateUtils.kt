package com.interswitchng.interswitchpossdk.shared.services.iso8583.utils

import java.text.SimpleDateFormat
import java.util.*

internal object DateUtils {

    @JvmField
    val timeAndDateFormatter = SimpleDateFormat("MMddhhmmss", Locale.getDefault()) // field 7
    @JvmField
    val timeFormatter = SimpleDateFormat("hhmmss", Locale.getDefault()) // field 12
    @JvmField
    val dateFormatter = SimpleDateFormat("MMdd", Locale.getDefault()) // field 13
    @JvmField
    val dateStringFormatter = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH)

}