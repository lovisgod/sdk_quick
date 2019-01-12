package com.interswitchng.interswitchpossdk.base

import android.content.Intent
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo

abstract class BaseTestActivity {
    private val paymentInfo = PaymentInfo(2000)
    val intent = Intent().apply {
        putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
    }
}