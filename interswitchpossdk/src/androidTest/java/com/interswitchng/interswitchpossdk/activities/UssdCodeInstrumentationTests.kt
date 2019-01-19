package com.interswitchng.interswitchpossdk.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.widget.TextView
import com.google.gson.Gson
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.mockservices.MockPayableService
import com.interswitchng.interswitchpossdk.modules.ussdqr.activities.UssdActivity
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.utils.Utilities
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Assert
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext

class UssdCodeInstrumentationTests: BaseTestActivity() {

    @Rule @JvmField
    val activityRule = ActivityTestRule<UssdActivity>(UssdActivity::class.java, true, false)

    private fun startWorkflow() {

        activityRule.launchActivity(intent)

        WaitUtils.waitTime(500)
        onView(withId(R.id.banks)).perform(ViewActions.click())

        WaitUtils.waitTime(500)
        onView(ViewMatchers.withText("GUARANTY TRUST BANK")).perform(ViewActions.click())

        WaitUtils.waitTime(500)
        onView(withId(R.id.btnGetCode)).perform(ViewActions.click())
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

    @Test
    fun should_show_success_message_when_transaction_is_successful() {

        val service: Payable = MockPayableService.Builder()
                .setPaymentStatusCall {
                    val successString = Utilities.getJson(activityRule.activity, "success-transaction-response.json")
                    val transaction: Transaction = Gson().fromJson(successString, Transaction::class.java)
                    it.onTransactionCompleted(transaction)
                }
                .build()

        StandAloneContext.loadKoinModules(module(override = true) {
            single { service }
        })

        startWorkflow()

        WaitUtils.waitTime(3000)
        onView(withText(R.string.title_checking_transaction_status)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }
        WaitUtils.cleanupWaitTime()


        WaitUtils.waitTime(1000)
        WaitUtils.cleanupWaitTime()
        onView(withText(R.string.title_transaction_completed_successfully)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }

    }

    @Test
    fun should_show_error_msg_when_transaction_fails() {

        var errorMsg = "error"
        val service: Payable = MockPayableService.Builder()
                .setPaymentStatusCall {
                    val successString = Utilities.getJson(activityRule.activity,"failed-transaction-response.json")
                    val transaction: Transaction = Gson().fromJson(successString, Transaction::class.java)
                    errorMsg = transaction.responseDescription ?: "error"
                    it.onTransactionError(transaction, null)
                }
                .build()

        StandAloneContext.loadKoinModules(module(override = true) {
            single { service }
        })


        startWorkflow()

        WaitUtils.waitTime( 500)
        onView(withText(R.string.title_checking_transaction_status)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }

        WaitUtils.waitTime(1000)
        onView(withText(errorMsg)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }
    }

    @Test
    fun should_show_error_msg_when_transaction_times_out() {

        val service: Payable = MockPayableService.Builder()
                .setPaymentStatusCall { it.onTransactionTimeOut() }
                .build()

        StandAloneContext.loadKoinModules(module(override = true) {
            single { service }
        })


        startWorkflow()

        WaitUtils.waitTime( 500)
        onView(withText(R.string.title_checking_transaction_status)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }

        WaitUtils.waitTime(1000)
        onView(withText(R.string.content_transaction_in_progress_time_out)).check { view, noViewFoundException ->

            Assert.assertNull(noViewFoundException)

            Assert.assertTrue(view.visibility == View.VISIBLE)
        }

        WaitUtils.cleanupWaitTime()
    }

}