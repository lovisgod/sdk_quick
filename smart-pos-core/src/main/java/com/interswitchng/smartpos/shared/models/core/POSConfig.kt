package com.interswitchng.smartpos.shared.models.core

import com.interswitchng.smartpos.shared.interfaces.library.UsbConnector


/**
 * A data model representing the the POS configuration for the current acquirer
 */
data class POSConfig(
        internal val alias: String,
        internal val clientId: String,
        internal val clientSecret: String,
        internal val merchantCode: String,
        internal val merchantTelephone: String = "") {

    internal var usbConnector: UsbConnector? = null

    fun with(usb: UsbConnector): POSConfig {
        usbConnector = usb
        return this
    }
}