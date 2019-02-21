package com.interswitchng.com.app.usb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.emv.pax.services.POSDeviceService
import com.interswitchng.smartpos.shared.errors.NotConfiguredException
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

        try {
            val deviceService = POSDeviceService.create(this)
            val config = POSConfig("MX5882").with(usbConfig)
            // configure terminal
            IswPos.configureTerminal(application, deviceService, config)
        } catch (ex: NotConfiguredException) {
            Toast.makeText(this, "Terminal not configured, use menu to configure terminal", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        usbConfig.startService(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.terminal_config) {
            IswPos.showSettingsScreen() // show settings for terminal configuration
            return true
        }

        return super.onOptionsItemSelected(item)
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
