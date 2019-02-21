package com.interswitchng.smartpos.shared.models.core

import com.interswitchng.smartpos.shared.interfaces.library.UsbConnector

data class POSConfig(internal val merchantCode: String) {

    internal var usbConnector: UsbConnector? = null

    fun with(usb: UsbConnector): POSConfig {
        usbConnector = usb
        return this
    }
}