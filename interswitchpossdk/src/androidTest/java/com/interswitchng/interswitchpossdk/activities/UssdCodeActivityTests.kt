package com.interswitchng.interswitchpossdk.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.widget.TextView
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.UssdActivity
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UssdCodeActivityTests: BaseTestActivity() {

    @Rule @JvmField
    val activityRule = ActivityTestRule<UssdActivity>(UssdActivity::class.java, true, false)

    @Before
    fun setup() {
        activityRule.launchActivity(intent)
    }

    @Test
    fun should_show_ussd_code_when_successful() {
        startWorkflow()

        WaitUtils.waitTime(1000)

        onView(withId(R.id.ussdText)).check { view, noViewFoundException ->
            assertNull(noViewFoundException)

            assertTrue((view as TextView).text.isNotEmpty())
            assertTrue(view.text.isNotBlank())
        }

        WaitUtils.waitTime(2000)
        onView(withText("Transaction in progress")).check(matches(isDisplayed()))

        WaitUtils.waitTime(4000)
        onView(withText("Transaction completed successfully")).check { view, noViewFoundException ->

            assertNull(noViewFoundException)

            assertTrue(view.visibility == View.VISIBLE)
        }

        WaitUtils.cleanupWaitTime()
    }

    private fun startWorkflow() {

        WaitUtils.waitTime(500)
        onView(withId(R.id.banks)).perform(ViewActions.click())

        WaitUtils.waitTime(500)
        onView(ViewMatchers.withText("GUARANTY TRUST BANK")).perform(ViewActions.click())

        WaitUtils.waitTime(500)
        onView(withId(R.id.btnGetCode)).perform(ViewActions.click())
    }
}