package com.interswitchng.interswitchpossdk.shared.models.posconfig

import android.graphics.Bitmap


internal sealed class PrintObject {
    class Line: PrintObject()
    class Data(val data: String): PrintObject()
    class BitMap(val bitmap: Bitmap): PrintObject()
}
