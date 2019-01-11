package com.interswitchng.interswitchpossdk

import android.support.test.annotation.UiThreadTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeActivityTests: BaseActivityTest() {

    @Rule @JvmField
    val activityRule = ActivityTestRule(QrCodeActivity::class.java, true, false)


    @Before
    fun startWithCustomIntent() {
        activityRule.launchActivity(intent)
    }

    @Test
    @UiThreadTest
    fun should_show_qr_code_image() {
        WaitUtils.waitTime(2000)

        WaitUtils.cleanupWaitTime()
    }

}