package com.interswitchng.interswitchpossdk

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ActivityWorkflows: BaseTestActivity() {

    @Rule @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setup() {
        activityRule.launchActivity(intent)
    }

    @Test
    fun show_qr_work_flow() {

        WaitUtils.waitTime(2000)

        onView(withId(R.id.qrPayment)).perform(click())

        WaitUtils.waitTime(3000)

        onView(withId(R.id.qrCodeImage)).check(matches(isDisplayed()))


        WaitUtils.cleanupWaitTime()
    }

}