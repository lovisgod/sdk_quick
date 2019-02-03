package com.interswitchng.interswitchpossdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.content_toolbar.*
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        toolbar.title = "Payment Method"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)
        // set up the UI
        DisplayUtils.setupPaymentOptions(this.window.decorView.findViewById(android.R.id.content), paymentInfo)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)

    }
}

