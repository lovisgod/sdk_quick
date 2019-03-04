package com.interswitchng.smartpos.emv.pax.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import com.interswitchng.smartpos.emv.pax.R
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.smartpos.shared.interfaces.device.IPrinter
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
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

    fun setCompanyLogo(bitmap: Bitmap) {
        companyLogo = bitmap
    }


    companion object {

        internal lateinit var companyLogo: Bitmap private set

        @JvmStatic
        internal fun create(context: Context, printer: IPrinter = Printer, factory: () -> EmvCardTransaction): POSDeviceService {
            // setupDevice pos device
            setupDevice(context)

            // setup logo
            val drawable = ContextCompat.getDrawable(context, R.drawable.isw_pax_app_logo)!!
            companyLogo = run {
                return@run when(drawable) {
                    is BitmapDrawable -> drawable.bitmap
                    else -> Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                    }
                }
            }

            return POSDeviceService(printer, factory)
        }


        @JvmStatic
        fun create(context: Context): POSDeviceService {
            val printer: IPrinter = Printer
            val factory = { EmvTransactionService(context) }
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