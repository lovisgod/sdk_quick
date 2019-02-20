package com.interswitchng.smartpos.shared.services.iso8583.utils

import java.text.SimpleDateFormat
import java.util.*

internal object DateUtils {

    @JvmField
    val timeAndDateFormatter = SimpleDateFormat("MMddHHmmss", Locale.getDefault()) // field 7
    @JvmField
    val timeFormatter = SimpleDateFormat("HHmmss", Locale.getDefault()) // field 12
    @JvmField
    val dateFormatter = SimpleDateFormat("MMdd", Locale.getDefault()) // field 13
    @JvmField
    val dateStringFormatter = SimpleDateFormat("YYYY-MM-dd hh:mm:ss", Locale.ENGLISH)

}