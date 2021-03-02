package com.interswitchng.smartpos.modules.main.transfer.utils
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Base64
import java.net.URLEncoder

object CipherUtil {
    fun generateSignatureCipherPlain(httpMethod: String, url:String, nonce:String, clientId:String, clientSecret: String): String {
        return httpMethod + "&" + urIEncode(url) + "&" + getTimeStamp() + "&" + nonce + "&" + clientId + "&" + clientSecret
    }

    fun urIEncode(url: String): String {
        return URLEncoder.encode(url, "utf-8")
    }

    fun generateCipherHash(ci: String): String {
        val bytes: ByteArray = ci.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)

//        return Base64.getEncoder().encodeToString(bytes)
    }

    fun getTimeStamp():Long {
        return System.currentTimeMillis() / 1000 or 0
    }
}