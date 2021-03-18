package com.interswitchng.smartpos.modules.main.transfer

import android.content.*
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.interswitchng.smartpos.R
import com.pixplicity.easyprefs.library.Prefs

class TransferActivity : AppCompatActivity() {
    private val navController by lazy { findNavController(R.id.isw_navigation_fragment) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_transfer)

        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }

//    private val usbStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//           val action = intent.action
//           if(action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
//               Toast.makeText(context, "USB connected please remove the usb to remove this app", Toast.LENGTH_LONG).show()
//           }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        registerReceiver(usbStateReceiver, IntentFilter("usb_detect"))
//    }
//
//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(usbStateReceiver)
//    }
}