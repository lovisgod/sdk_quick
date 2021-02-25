package com.interswitchng.smartpos.modules.main.transfer.utils
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

object CipherUtil {
    fun generateSignatureCipherPlain(httpMethod: String, url:String, nonce:String, clientId:String, clientSecret: String): String {
        return httpMethod + "&" + url + "&" + getTimeStamp() + "&" + nonce + "&" + clientId + "&" + clientSecret
    }

    fun getTimeStamp():Long {
        return System.currentTimeMillis() / 1000 or 0
    }
}