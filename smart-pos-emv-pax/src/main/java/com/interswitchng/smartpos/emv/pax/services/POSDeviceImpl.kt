package com.interswitchng.smartpos.emv.pax.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import com.interswitchng.smartpos.emv.pax.R
import com.interswitchng.smartpos.shared.interfaces.device.EmvCardReader
import com.interswitchng.smartpos.shared.interfaces.device.DevicePrinter
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.emv.pax.emv.DeviceImplNeptune
import com.pax.dal.IDAL
import com.pax.jemv.device.DeviceManager
import com.pax.neptunelite.api.NeptuneLiteUser


/**
 * This class provides Implemenation for the [POSDevice]
 */
class POSDeviceImpl private constructor(override val printer: DevicePrinter, private val factory: () -> EmvCardReader) : POSDevice {


    init {
        System.loadLibrary("F_DEVICE_LIB_PayDroid")
        System.loadLibrary("F_EMV_LIB_PayDroid")
        System.loadLibrary("F_ENTRY_LIB_PayDroid")
        System.loadLibrary("JNI_EMV_v100")
        System.loadLibrary("JNI_ENTRY_v100")
        System.loadLibrary("F_PUBLIC_LIB_PayDroid")
    }


    override fun getEmvCardReader(): EmvCardReader = factory()

    fun setCompanyLogo(bitmap: Bitmap) {
        companyLogo = bitmap


    }


    companion object {

        internal lateinit var companyLogo: Bitmap private set

        @JvmStatic
        internal fun create(context: Context, printer: DevicePrinter = DevicePrinterImpl, factory: () -> EmvCardReader): POSDeviceImpl {
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

            return POSDeviceImpl(printer, factory)
        }


        @JvmStatic
        fun create(context: Context): POSDeviceImpl {
            val printer: DevicePrinter = DevicePrinterImpl
            val factory = { EmvCardReaderImpl(context) }
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