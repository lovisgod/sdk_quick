package com.interswitchng.smartpos.usb.interfaces

interface UsbConnector {

    fun open(): Boolean

    fun close(): Boolean

    fun send(message: String): Boolean

    fun receive(): String

    fun sendReceive(): String
}