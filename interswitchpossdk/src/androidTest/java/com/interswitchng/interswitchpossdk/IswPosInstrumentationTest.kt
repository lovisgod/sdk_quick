package com.interswitchng.interswitchpossdk

import android.app.Application
import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.shared.models.POSConfiguration
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IswPosInstrumentationTest {

    private val monitor = Instrumentation.ActivityMonitor(MainActivity::class.java.simpleName, null, true)

    @Before
    fun setup() {
        InstrumentationRegistry.getInstrumentation().addMonitor(monitor)

        val app = InstrumentationRegistry.getTargetContext().applicationContext as Application
        val config = POSConfiguration()
        IswPos.configureTerminal(app, config)
    }


    @Test
    fun should_start_main_activity_when_payment_is_initiated() {

        val paymentInfo = PaymentInfo(20000)
        IswPos.getInstance().initiatePayment(paymentInfo)

        val mainActivity = monitor.waitForActivityWithTimeout(10 * 1000) // wait for 10 seconds

        assertNotNull("activity is null", mainActivity)
        assertTrue("activity created is not MainActivity", mainActivity is MainActivity)
    }

}