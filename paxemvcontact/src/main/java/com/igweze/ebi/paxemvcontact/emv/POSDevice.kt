package com.igweze.ebi.paxemvcontact.emv

import android.content.Context
import com.pax.dal.IDAL
import com.pax.neptunelite.api.NeptuneLiteUser

object POSDevice {

    init {
        System.loadLibrary("F_DEVICE_LIB_PayDroid")
        System.loadLibrary("F_EMV_LIB_PayDroid")
        System.loadLibrary("F_ENTRY_LIB_PayDroid")
        System.loadLibrary("JNI_EMV_v100")
        System.loadLibrary("JNI_ENTRY_v100")
        System.loadLibrary("F_PUBLIC_LIB_PayDroid")
    }

    @JvmStatic
    lateinit var dal: IDAL private set

    @JvmStatic
    fun setup(context: Context) {
        val neptunes = NeptuneLiteUser.getInstance()
        dal = neptunes.getDal(context)
    }

}