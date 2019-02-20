package com.interswitchng.smartpos.usb

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.interswitchng.smartpos.usb.interfaces.UsbConnector
import com.interswitchng.smartpos.usb.utils.Constants.COMMAND_START_SERVICE
import com.interswitchng.smartpos.usb.utils.Constants.KEY_SERVICE_COMMAND
import com.interswitchng.smartpos.usb.utils.NotificationUtil
import org.koin.android.ext.android.inject

class UsbService: Service() {
    // flag to check if service has been started
    private var mServiceIsStarted = false

    private val mainHandler = Handler(Looper.getMainLooper())

    private val logger = Logger.with(UsbService::class.toString())
    private val usbConnector: UsbConnector by inject()

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        logger.log("OnDestroy Synchronization service")
        if (usbConnector.isOpen()) usbConnector.close()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // evaluate start command
        return intent.extras!!.let  {
            val command = it.get(KEY_SERVICE_COMMAND) as Int
            logger.log("Received command $command")
            // respond to service command
            return@let when(command) {
                COMMAND_START_SERVICE -> startSynchronization()
                else -> stopSynchronization()
            }
        }
    }

    private fun startSynchronization(): Int {
        // ensure service has not been started yet
        if (!mServiceIsStarted) {
            val msg = "Starting Synchronization service"
            logger.log(msg)

            // check build version to determine what type of notification to build
            val isPreAndroidO = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1

            // lunch notification depending on device version
            if (isPreAndroidO) NotificationUtil.PreO.createNotification(this)
            else NotificationUtil.O.createNotification(this)

            // start receiving commands
            startReceivingCommands()

            // set flag for started service
            mServiceIsStarted = true
        }

        return START_REDELIVER_INTENT
    }

    private fun stopSynchronization(): Int {
        if (mServiceIsStarted) {
            logger.log("Stopping Synchronization service")
            mServiceIsStarted = false
            stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startReceivingCommands() {
        Thread {
            usbConnector.open()
            makeToast("A connection has been made!!!")

            while (mServiceIsStarted) {
                // receive message
                val message = usbConnector.receive()
                makeToast(message)
            }
        }.start()
    }

    private fun makeToast(msg: String) {
        mainHandler.post { Toast.makeText(this, msg, Toast.LENGTH_LONG).show() }
    }
}
