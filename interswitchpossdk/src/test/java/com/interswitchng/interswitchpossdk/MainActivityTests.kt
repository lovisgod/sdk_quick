package com.interswitchng.interswitchpossdk

import android.app.Application
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import android.content.Intent
import android.provider.SyncStateContract
import android.widget.LinearLayout
import androidx.test.core.app.ApplicationProvider
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.UssdActivity
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class MainActivityTests {

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        val parcel = PaymentInfo(2000)
        val mainIntent = Intent().apply {
            putExtra(KEY_PAYMENT_INFO, parcel)
        }
        activity = Robolectric.buildActivity(MainActivity::class.java, mainIntent).create().get()
    }


    @Test
    fun `should start qr-code activity when qr-button is clicked`() {

        activity.findViewById<LinearLayout>(R.id.qrPayment).performClick()

        val expectedIntent = Intent(activity, QrCodeActivity::class.java)
        val app: Application =  ApplicationProvider.getApplicationContext()
        val actual = shadowOf(app).nextStartedActivity

        assertEquals(expectedIntent.component, actual.component)
    }

    @Test
    fun `should start ussd activity when ussd button is clicked`() {

        activity.findViewById<LinearLayout>(R.id.ussdPayment).performClick()

        val expectedIntent = Intent(activity, UssdActivity::class.java)
        val app: Application =  ApplicationProvider.getApplicationContext()
        val actual = shadowOf(app).nextStartedActivity

        assertEquals(expectedIntent.component, actual.component)
    }
}