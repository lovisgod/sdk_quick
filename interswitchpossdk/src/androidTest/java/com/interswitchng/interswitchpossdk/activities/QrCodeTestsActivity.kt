package com.interswitchng.interswitchpossdk.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.base.MockApplication
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeTestsActivity: BaseTestActivity() {

    @Rule @JvmField
    val activityRule = ActivityTestRule(QrCodeActivity::class.java, true, false)


    @Before
    fun startWithCustomIntent() {
        // don't mock
        activityRule.launchActivity(intent)
    }

    @Test
    fun should_show_qr_code_image() {
        WaitUtils.waitTime(1000)

        onView(withText(R.string.title_processing_payment)).perform(click())
        onView(withText(R.string.title_processing_payment)).check(matches(isDisplayed()))

        WaitUtils.waitTime(2000)
        onView(withId(R.id.qrCodeImage)).check(matches(isDisplayed()))

        WaitUtils.cleanupWaitTime()
    }

    @Test
    fun should_show_error_msg() {
        WaitUtils.waitTime(1000)


    }

}