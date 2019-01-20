package com.interswitchng.interswitchpossdk.shared.models.posconfig

import android.graphics.Bitmap


internal class PrintConfiguration {

    private val printObjects = mutableListOf<PrintObject>()

    fun addData(data: String): PrintConfiguration {
        printObjects.add(PrintObject.Data(data))
        return this
    }

    fun addBitmap(bitmap: Bitmap): PrintConfiguration {
        printObjects.add(PrintObject.BitMap(bitmap))
        return this
    }

    fun addLine(): PrintConfiguration {
        printObjects.add(PrintObject.Line())
        return this
    }

    fun getPrintObjects(): List<Any> {
        return printObjects
    }
}