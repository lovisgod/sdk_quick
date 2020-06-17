package com.interswitchng.smartpos.shared.services.iso8583.utils

import java.text.SimpleDateFormat
import java.util.*

internal object DateUtils {

    @JvmField
    val timeAndDateFormatter = SimpleDateFormat("MMddHHmmss") // field 7

    @JvmField
    val timeFormatter = SimpleDateFormat("HHmmss") // field 12

    @JvmField
    val monthFormatter = SimpleDateFormat("MMdd") // field 13

    val dateFormatter = SimpleDateFormat("yyMMdd")

    val yearAndMonthFormatter = SimpleDateFormat("yyMM")

    val timeOfDateFormat = SimpleDateFormat("hh:mm aa, MMMM dd, yyyy")

    val shortDateFormat = SimpleDateFormat( "dd MMMM, yyyy")

    val universalDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") }

    val universalDateFormatNew by lazy { SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ssXXX") }

    val hourMinuteFormat by lazy { SimpleDateFormat("HH:mm", Locale.ROOT) }



}