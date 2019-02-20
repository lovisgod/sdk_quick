package com.interswitchng.smartpos.shared.interfaces.library

internal interface IsoSocket {

    fun setTimeout(timeout: Int)

    fun open(): Boolean

    fun close()

    fun send(bytes: ByteArray): Boolean

    fun receive(): ByteArray?

    fun sendReceive(bytes: ByteArray): ByteArray?
}