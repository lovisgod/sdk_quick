package com.interswitchng.interswitchpossdk

import android.content.Intent
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseActivityTest {
    private val paymentInfo = PaymentInfo(2000)
    val intent = Intent().apply {
        putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
    }
}