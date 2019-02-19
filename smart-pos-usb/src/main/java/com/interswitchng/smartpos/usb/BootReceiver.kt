package com.interswitchng.smartpos.usb

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.interswitchng.smartpos.usb.utils.Constants.COMMAND_START_SERVICE
import com.interswitchng.smartpos.usb.utils.Constants.KEY_SERVICE_COMMAND
import org.koin.standalone.KoinComponent

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val logger = Logger.with("BootReceiver")

    @TargetApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        logger.log("Boot complete intent received")

        // get the triggered actions
        val action: String = intent.action ?: return

        // check if action is a boot action
        val isBootComplete = listOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_REBOOT, Intent.ACTION_LOCKED_BOOT_COMPLETED).contains(action)
        if (isBootComplete) {
            logger.log("Boot Intent action: ${intent.action}")

            // log information to fireBase of bootReceiver starting service
            logger.log("Starting Usb Service from BootReceiver")

            // start service with intent
            startUsbService(context)

            // show message for started service
            Toast.makeText(context, "usb service started", Toast.LENGTH_LONG).show()
            logger.log("Services started")
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun startUsbService(context: Context) {
        // create intent to start service
        val intent = Intent(context, UsbService::class.java).apply {
            putExtra(KEY_SERVICE_COMMAND, COMMAND_START_SERVICE)
        }

        // check build version to determine what type of notification to build
        val isPreAndroidO = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1

        // start service
        if (isPreAndroidO) context.startService(intent)
        else ContextCompat.startForegroundService(context, intent)
    }
}
