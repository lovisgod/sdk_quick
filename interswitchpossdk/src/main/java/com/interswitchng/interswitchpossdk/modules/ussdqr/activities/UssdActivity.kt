package com.interswitchng.interswitchpossdk.modules.ussdqr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import kotlinx.android.synthetic.main.activity_ussd.*
import org.koin.android.ext.android.inject

class UssdActivity : AppCompatActivity() {

    private val paymentService: Payable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ussd)
        setSupportActionBar(toolbar)

        val paymentInfo: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
    }

}
