package com.interswitchng.interswitchpossdk.shared.models.core

import com.interswitchng.interswitchpossdk.shared.interfaces.library.UsbConnector

data class POSConfig(internal val merchantCode: String) {

    internal var usbConnector: UsbConnector? = null

    fun with(usb: UsbConnector): POSConfig {
        usbConnector = usb
        return this
    }
}