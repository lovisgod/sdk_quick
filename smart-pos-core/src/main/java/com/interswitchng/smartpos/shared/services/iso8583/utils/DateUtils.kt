package com.interswitchng.smartpos.shared.services.iso8583.utils

import java.text.SimpleDateFormat
import java.util.*

internal object DateUtils {

    @JvmField
    val timeAndDateFormatter = SimpleDateFormat("MMddHHmmss", Locale.ENGLISH) // field 7

    @JvmField
    val timeFormatter = SimpleDateFormat("HHmmss", Locale.ENGLISH) // field 12

    @JvmField
    val monthFormatter = SimpleDateFormat("MMdd", Locale.ENGLISH) // field 13

    val dateFormatter = SimpleDateFormat("yyMMdd", Locale.ENGLISH)

    val yearAndMonthFormatter = SimpleDateFormat("yyMM", Locale.ENGLISH)

    val timeOfDateFormat = SimpleDateFormat("hh:mm aa, MMMM dd, YYYY", Locale.ENGLISH)

    val shortDateFormat = SimpleDateFormat( "dd MMMM, yyyy", Locale.ENGLISH)

}