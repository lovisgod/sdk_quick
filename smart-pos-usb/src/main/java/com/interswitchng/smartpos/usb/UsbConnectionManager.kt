package com.interswitchng.smartpos.usb

import com.interswitchng.smartpos.usb.interfaces.UsbConnector
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

typealias ResponseListener = (String?) -> Unit

class UsbConnectionManager: UsbConnector {

    // executor for async message send/receive
    private val executor = Executors.newCachedThreadPool()
    // connected client socket
    private var mClientSocket: Socket? = null

    companion object {

        const val LISTENING_PORT = 39300
    }

    // read data from socket's inputstream
    override fun receive(): String {

        val inputStream = mClientSocket?.getInputStream() ?: return ""
        val reader = BufferedReader(InputStreamReader(inputStream))

        return reader.readLine()
    }

    // send message, then wait for response
    @Throws(IOException::class)
    override fun sendReceive(message: String): String {
        send(message)
        return receive()
    }

    // create server and listen for client(USB) connection
    override fun open(): Boolean {

        val serverSocket = ServerSocket(LISTENING_PORT)
        mClientSocket = serverSocket.accept()
        return true
    }

    // close connection
    override fun close(): Boolean {

        mClientSocket?.close()
        return true
    }

    // send message to the connected USB client
    override fun send(message: String): Boolean {

        val outputStream = mClientSocket?.getOutputStream() ?: return false
        val writer = PrintWriter(outputStream, true)
        writer.println(message + "\n")
        return true
    }

    // send message asynchronously to the connected USB client
    override fun sendAsync(message: String) {
        executor.execute {
            send(message)
        }
    }

    // receive message asynchronously
    override fun receiveAsync(listener: ResponseListener) {

        executor.execute {
            val data = receive()
            listener(data)
        }
    }
}