package com.interswitchng.smartpos.modules.main.transfer.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.MessageDigest
import java.util.*

object HashUtils {
    fun sha512(input: String) = hashString("SHA-512", input)

    fun sha256(input: String) = hashString("SHA-256", input)

    fun sha1(input: String) = hashString("SHA-1", input)

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateCipherHash(ci: String): String {
        val bytes = ci.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789ABCDEF"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())
        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

    private fun hexgenerator():String {
        val randomValue = Math.floor((1 + Math.random()) * 0x10000)
        return Integer.toHexString(randomValue.toInt())
    }

    fun generateGuid(repeat: Int): String {
        var guid = ""
        for (i in 1..repeat) {
            guid += hexgenerator()
        }
        return guid
    }
}