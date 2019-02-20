package com.interswitchng.com.app.usb

import android.app.Application
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService
import com.interswitchng.smartpos.usb.UsbConfig

class UsbApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        val deviceService = POSDeviceService.create(this)
        val usbConfig = UsbConfig
        val config = POSConfig("MX5882").with(usbConfig)
        // configure terminal
        IswPos.configureTerminal(this, deviceService, config)
    }
}