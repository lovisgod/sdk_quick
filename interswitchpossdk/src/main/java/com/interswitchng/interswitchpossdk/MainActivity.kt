package com.interswitchng.interswitchpossdk

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.UssdActivity
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)
        // set up the UI
        setupUI(paymentInfo)
    }

    private fun setupUI(info: PaymentInfo) {
        ussdPayment.setOnClickListener {
            val ussdIntent = Intent(this, UssdActivity::class.java)
            ussdIntent.putExtra(KEY_PAYMENT_INFO, info)
            startActivity(ussdIntent)
        }

        qrPayment.setOnClickListener {
            val qrIntent = Intent(this, QrCodeActivity::class.java)
            qrIntent.putExtra(KEY_PAYMENT_INFO, info)
            startActivity(qrIntent)
        }
    }
}
