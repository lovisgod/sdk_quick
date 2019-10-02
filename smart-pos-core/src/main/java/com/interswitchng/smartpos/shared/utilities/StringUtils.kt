package com.interswitchng.smartpos.shared.utilities

import java.util.*

fun String.removeComma(): String {
    var cont = ""
    for (i in this) {
        if ((i == ',').not()) {
            cont += i
        }
    }

    return cont
}

fun String.addComma(): String {
    var digitsBeforeDot = ""
    var digitsAfterDot = ""

    if (this.contains(".")) {
        digitsBeforeDot = this.substringBefore(".")
        digitsAfterDot = this.substringAfter(".")
    } else {
        digitsBeforeDot = this
    }



    val size = digitsBeforeDot.length
    val stack = Stack<Int>()

    for (i in size-4 downTo  0 step 3) {
        stack.push(i)
    }

    var container = ""
    var start = 0
    while (stack.isNotEmpty()) {
        val position = stack.pop()
        container = container +  digitsBeforeDot.substring(start .. position) + ","
        start = position + 1
    }


    if (this.contains(".")) {
        container = container + digitsBeforeDot.substring(size-3 until size) + "." +  digitsAfterDot
    } else {
        container += digitsBeforeDot.substring(size-3 until size)
    }

    return container
}