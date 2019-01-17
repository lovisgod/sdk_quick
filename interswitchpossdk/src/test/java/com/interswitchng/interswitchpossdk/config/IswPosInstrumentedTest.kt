package com.interswitchng.interswitchpossdk.config

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.MainActivity
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
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
        IswPos.configureTerminal(app, mock())

        val expectedIntent = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(KEY_PAYMENT_INFO, payment)
        }
        val expectedIntent2 = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(KEY_PAYMENT_INFO, payment)
        }

        IswPos.getInstance().initiatePayment(payment)
        val actual = Shadows.shadowOf(app).nextStartedActivity

        val isSame = expectedIntent2 == expectedIntent

        assertEquals("Main Activity was not lunched", expectedIntent.component, actual.component)
    }

}