package com.interswitchng.smartpos.shared.models.posconfig

import android.graphics.Bitmap


sealed class PrintObject {
    class Line: PrintObject()
    class Data(val value: String, val config: PrintStringConfiguration = PrintStringConfiguration()): PrintObject()
    class BitMap(val bitmap: Bitmap): PrintObject()
}
