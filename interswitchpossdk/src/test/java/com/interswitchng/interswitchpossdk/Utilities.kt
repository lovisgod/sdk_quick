package com.interswitchng.interswitchpossdk

import java.io.File

object Utilities {

    fun getJson(path: String): String {
        val uri = javaClass.classLoader?.getResource(path)
        val file = File(uri?.path)
        return String(file.readBytes())
    }
}
