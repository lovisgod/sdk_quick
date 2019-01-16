package com.interswitchng.interswitchpossdk.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.base.MockApplication
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4::class)
class QrCodeTestsActivity: BaseTestActivity() {

    @Rule @JvmField
    val activityRule = ActivityTestRule(QrCodeActivity::class.java, true, false)


    @Test
    fun should_show_qr_code_image() {
        activityRule.launchActivity(intent)

        onView(withText(R.string.title_processing_payment)).check(matches(isDisplayed()))

        WaitUtils.waitTime(2000)
        onView(withId(R.id.qrCodeImage)).check(matches(isDisplayed()))

        WaitUtils.cleanupWaitTime()
    }

    @Test
    fun should_show_success_message_when_transaction_is_successful() {
//        val service : Payable = mock()
        loadKoinModules(module(override = true) {
//            single { service }
        })

        activityRule.launchActivity(intent)

        WaitUtils.waitTime(3000)
        onView(withText(R.string.title_processing_payment)).check(matches(isDisplayed()))

        WaitUtils.waitTime(3000)
        onView(withText(R.string.title_transaction_completed_successfully)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }
    }

    @Test
    fun should_show_error_msg() {
        activityRule.launchActivity(intent)

        WaitUtils.waitTime(1000)

        WaitUtils.waitTime(3000)
        onView(withText(R.string.title_transaction_not_confirmed)).check(matches(isDisplayed()))

        WaitUtils.waitTime(3000)
        onView(withText(R.string.content_transaction_not_confirmed)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }
    }

    @Test
    fun should_terminate_polling_when_transaction_takes_over_5_minutes() {

    }

}