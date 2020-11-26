package com.interswitchng.smartpos.shared.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.reflect.Method


object DeviceUtils {

    const val i2G = "2G"
    const val i3G = "3G"
    const val i4G = "4G"
    const val UNKNOWN = "UNKNOWN"


    // live data value to observe network state
    private val networkConnectionState = MutableLiveData<Boolean>()
    fun getNetworkConnectionState(): LiveData<Boolean> = networkConnectionState


    private fun checkNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        // should check null because in airplane mode it will be null
        return netInfo != null && netInfo.isConnected
    }

    fun getNetworkClass(context: Context): String {
        val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkType = mTelephonyManager.networkType
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> i2G
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> i3G
            TelephonyManager.NETWORK_TYPE_LTE -> i4G
            else -> UNKNOWN
        }
    }

    fun isConnectedToInternet(context: Context) = checkNetwork(context).also { networkConnectionState.postValue(it) }

    @SuppressLint("PrivateApi")
    fun getSerialNumber(): String? {
        var serialNumber: String?
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get: Method = c.getMethod("get", String::class.java)
            serialNumber = get.invoke(c, "gsm.sn1").toString()
            if (serialNumber == "") serialNumber = get.invoke(c, "ril.serialnumber").toString()
            if (serialNumber == "") serialNumber = get.invoke(c, "ro.serialno").toString()
            if (serialNumber == "") serialNumber = get.invoke(c, "sys.serialnumber").toString()
            if (serialNumber == "") serialNumber = Build.SERIAL

            // If none of the methods above worked
            if (serialNumber == "") serialNumber = null
        } catch (e: Exception) {
            e.printStackTrace()
            serialNumber = null
        }
        return serialNumber
    }
}
