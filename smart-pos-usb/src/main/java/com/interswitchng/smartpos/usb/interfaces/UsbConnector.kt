package com.interswitchng.smartpos.usb.interfaces

import com.interswitchng.smartpos.usb.ResponseListener

interface UsbConnector {

    fun open(): Boolean

    fun close(): Boolean

    fun send(message: String): Boolean

    fun receive(): String

    fun sendReceive(message: String): String

    fun sendAsync(message: String)

    fun receiveAsync(listener: ResponseListener)
}