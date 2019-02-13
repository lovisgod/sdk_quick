package com.igweze.ebi.paxemvcontact

import java.io.File
import java.io.InputStream

object Utilities {

    fun getStream(path: String): InputStream {
        val loader = javaClass.classLoader
        val uri = loader?.getResource(path)
        val file = File(uri?.path)
        return file.inputStream()
    }

    fun getJson(path: String): String {
        val uri = javaClass.classLoader?.getResource(path)
        val file = File(uri?.path)
        return String(file.readBytes())
    }

    fun getBytes(path: String): ByteArray {
        val uri = javaClass.classLoader?.getResource(path)
        val file = File(uri?.path)
        return file.readBytes()
    }
}
