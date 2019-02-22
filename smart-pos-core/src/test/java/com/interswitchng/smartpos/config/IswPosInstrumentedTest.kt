package com.interswitchng.smartpos.config

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.modules.home.HomeActivity
import com.interswitchng.smartpos.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class IswPosInstrumentedTest {

    @Test
    fun should_start_main_activity_when_payment_is_initiated() {
        val app: Application =  ApplicationProvider.getApplicationContext()
        val payment: PaymentInfo = mock()
        val config: POSConfig = mock()
        IswPos.setupTerminal(app, mock(), config)
        val activity: Activity =  mock()

        val expectedIntent = Intent(app, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(KEY_PAYMENT_INFO, payment)
        }
        val expectedIntent2 = Intent(app, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(KEY_PAYMENT_INFO, payment)
        }

        IswPos.getInstance().initiatePayment(activity, 5000, null)
        val actual = Shadows.shadowOf(app).nextStartedActivity


        assertEquals("Main Activity was not lunched", expectedIntent.component, actual.component)
    }

}