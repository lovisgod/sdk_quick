package com.interswitchng.com.app.usb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.usb.UsbConfig
import com.interswitchng.smartpos.usb.interfaces.MessageListener

class UsbActivity : AppCompatActivity() , MessageListener {

    // configure usb with message listener
    private val usbConfig by lazy { UsbConfig(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)


        val deviceService = POSDeviceService.create(this)
        val config = POSConfig("MX5882").with(usbConfig)
        // configure terminal
        IswPos.configureTerminal(application, deviceService, config)
    }

    override fun onMessageReceived(paymentType: PaymentType, amount: Int) {
        IswPos.getInstance().initiatePayment(this, amount, paymentType)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            // handle success
            if (data != null) {
                val result = IswPos.getResult(data)
                Log.d("Demo", "" + result)
                usbConfig.handleResult(result)
            }
        } else {
            // else handle error
        }
    }
}
