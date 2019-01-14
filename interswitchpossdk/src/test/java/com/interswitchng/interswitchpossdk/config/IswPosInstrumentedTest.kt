package com.interswitchng.interswitchpossdk.config

import android.app.Instrumentation
import com.interswitchng.interswitchpossdk.MainActivity
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IswPosInstrumentedTest {

    private val monitor = Instrumentation.ActivityMonitor()

    @Test
    fun should_start_main_activity_when_payment_is_initiated() {


        val mainActivity = monitor.waitForActivityWithTimeout(10 * 1000)

        Assert.assertNotNull("activity is null", mainActivity)
        Assert.assertTrue("activity created is not MainActivity", mainActivity is MainActivity)
    }

}