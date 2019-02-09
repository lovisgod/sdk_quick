package com.interswitchng.interswitchpossdk.shared.services.iso8583.utils

import java.security.MessageDigest

object IsoUtils {

    @JvmStatic
    fun hexToBytes(hex: String): ByteArray {
        if (hex.length and 0x01 == 0x01)
            throw IllegalArgumentException()

        val bytes = ByteArray(hex.length / 2)
        for (idx in bytes.indices) {
            val hi = Character.digit(hex[idx * 2].toInt(), 16)
            val lo = Character.digit(hex[idx * 2 + 1].toInt(), 16)
            if (hi < 0 || lo < 0)
                throw IllegalArgumentException()
            bytes[idx] = (hi shl 4 or lo).toByte()
        }

        return bytes
    }

    @JvmStatic
    fun bytesToHex(bytes: ByteArray): String {

        val hex = CharArray(bytes.size * 2)
        for (idx in bytes.indices) {
            val hi = (bytes[idx].toInt() and 0xF0).ushr(4)
            val lo = bytes[idx].toInt() and 0x0F
            hex[idx * 2] = (if (hi < 10) '0'.toInt() + hi else 'A'.toInt() - 10 + hi).toChar()
            hex[idx * 2 + 1] = (if (lo < 10) '0'.toInt() + lo else 'A'.toInt() - 10 + lo).toChar()
        }
        return String(hex)
    }

    @Throws(Exception::class)
    @JvmStatic
    fun getMac(seed: String, macDataBytes: ByteArray): String {

        val keyBytes = hexToBytes(seed)
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(keyBytes, 0, keyBytes.size)
        digest.update(macDataBytes, 0, macDataBytes.size)
        val hashedBytes = digest.digest()
        var hashText = bytesToHex(hashedBytes)
        hashText = hashText.replace(" ", "")

        if (hashText.length < 64) {
            val numberOfZeroes = 64 - hashText.length
            var zeroes = ""
            var temp = hashText
            for (i in 0 until numberOfZeroes)
                zeroes += "0"
            temp = zeroes + temp
            return temp
        }

        return hashText
    }

}