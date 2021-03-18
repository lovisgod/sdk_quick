package com.interswitchng.smartpos.modules.main.transfer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.util.Log

class UsbReceiver: BroadcastReceiver() {
    val TAG = "status...."
    override fun onReceive(context: Context?, intent: Intent?) {
        val intent = Intent(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        context?.sendBroadcast(intent)
        Log.d(TAG, "USB connected")
    }
}