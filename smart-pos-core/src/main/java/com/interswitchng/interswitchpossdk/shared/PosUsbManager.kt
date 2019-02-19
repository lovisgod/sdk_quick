package com.interswitchng.interswitchpossdk.shared

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors

private const val USB_PERMISSION = "com.interswitchng.ACTION_REQUEST_USB_PERMISSION"

class PosUsbManager(val context: Context) {

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private val pendingIntent = PendingIntent.getBroadcast(context, 0, Intent(USB_PERMISSION), 0)

    // callback to receive messages
    var usbManagerMessageCallback: (String?) -> Unit = {}

    // error reporting callback
    var usbManagerErrorCallback: (Throwable?) -> Unit = {}

    // selected UsbAccessory
    private lateinit var usbAccessory: UsbAccessory
    private var parcelFileDescriptor: ParcelFileDescriptor? = null
    // inputStream used to read data from USB
    private var inputStream: FileInputStream? = null
    // outputStream used to send data to USB
    private var outputStream: FileOutputStream? = null
    // handler for sending messages/error through callback
    private val handler = Handler(Looper.getMainLooper())
    // thread executors
    private val executorService = Executors.newCachedThreadPool()

    init {

        val usbReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == USB_PERMISSION) {

                    val permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    if (permissionGranted) {

                        usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory
                        openAccessory()
                    }else {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // listen for USB permission request
        val filter = IntentFilter(USB_PERMISSION)
        context.registerReceiver(usbReceiver, filter)
    }

    // retrieve connected USB accessory
    // and request for permission if not
    // already granted.
    // if permission has already been granted
    // accessory would be opened for communication
    fun connectAccessory(intent: Intent?) {
        usbAccessory = intent?.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory
        if (!usbManager.hasPermission(usbAccessory)) {
            usbManager.requestPermission(usbAccessory, pendingIntent)
        }else {
            openAccessory()
        }
    }

    // send data to connected USB accessory
    fun sendData(data: String) {
        sendBytes(data.toByteArray())
    }

    fun sendBytes(data: ByteArray) {

        try {
            executorService.execute {

                outputStream?.write(data)
                outputStream?.flush()
            }
        }catch (e: Exception) {

            handler.post {
                usbManagerErrorCallback.invoke(e)
            }
        }
    }

    // open communication channels to accessory
    private fun openAccessory() {

        parcelFileDescriptor = usbManager.openAccessory(usbAccessory)
        parcelFileDescriptor?.fileDescriptor?.also {

            inputStream = FileInputStream(it)
            outputStream = FileOutputStream(it)

            executorService.execute {
                try {

                    val buffer = ByteArray(16384)
                    var i: Int? = 0

                    while (i != null && i > -1) {
                        i = inputStream?.read(buffer)
                    }

                    val data = buffer.toString()
                    handler.post {
                        usbManagerMessageCallback.invoke(data)
                    }
                } catch (e: Exception) {

                    handler.post {
                        usbManagerErrorCallback.invoke(e)
                    }
                }
            }
        }
    }
}