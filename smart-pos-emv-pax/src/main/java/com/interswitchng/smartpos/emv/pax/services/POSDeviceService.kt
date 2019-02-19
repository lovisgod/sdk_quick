package com.interswitchng.smartpos.emv.pax.services

import android.content.Context
import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.emv.pax.emv.DeviceImplNeptune
import com.pax.dal.IDAL
import com.pax.jemv.device.DeviceManager
import com.pax.neptunelite.api.NeptuneLiteUser

class POSDeviceService private constructor(override val printer: IPrinter, private val factory: () -> EmvCardTransaction) : POSDevice {


    init {
        System.loadLibrary("F_DEVICE_LIB_PayDroid")
        System.loadLibrary("F_EMV_LIB_PayDroid")
        System.loadLibrary("F_ENTRY_LIB_PayDroid")
        System.loadLibrary("JNI_EMV_v100")
        System.loadLibrary("JNI_ENTRY_v100")
        System.loadLibrary("F_PUBLIC_LIB_PayDroid")
    }

    override fun getEmvCardTransaction(): EmvCardTransaction = factory()

    companion object {
        @JvmStatic
        internal fun create(context: Context, printer: IPrinter = Printer, factory: () -> EmvCardTransaction): POSDeviceService {
            // setupDevice pos device
            setupDevice(context)
            return POSDeviceService(printer, factory)
        }


        @JvmStatic
        fun create(context: Context): POSDeviceService {
            val printer: IPrinter = Printer;
            val factory = { EmvTransactionService() }
            return create(context, printer, factory)
        }

        @JvmStatic
        lateinit var dal: IDAL
            private set

        private var isSetup = false

        private fun setupDevice(context: Context) {
            if (!isSetup) {
                val neptunes = NeptuneLiteUser.getInstance()
                dal = neptunes.getDal(context)
                // set IDevice implementation
                DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance())
                isSetup = true
            }
        }
    }
}