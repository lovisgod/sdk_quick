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

fun String.beforeDot(): String {
    return this.substringBefore('.')
}

fun String.center(size: Int, pad: Char = ' ', newLine: Boolean = false): String? {
    if (size <= this.length)
        return this

    val sb = StringBuilder(size)
    for (i in 0 until (size - this.length) / 2) {
        sb.append(pad)
    }
    sb.append(this)
    while (sb.length < size) {
        sb.append(pad)
    }

    if (newLine) sb.append("\n")
    return sb.toString()
}

fun String.addComma(): String {
    var digitsBeforeDot = ""
    var digitsAfterDot = ""

    if (this.contains(".")) {
        digitsBeforeDot = this.beforeDot()
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