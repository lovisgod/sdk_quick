package com.interswitchng.interswitchpossdk.activities

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.base.BaseTestActivity
import com.interswitchng.interswitchpossdk.modules.card.CardActivity
import com.interswitchng.interswitchpossdk.utils.WaitUtils
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardInstrumentationTests: BaseTestActivity() {

    @Rule @JvmField
    val activity = ActivityTestRule(CardActivity::class.java, true, false)


    @Before
    fun setup() {
        activity.launchActivity(intent)
    }

    @Test
    fun loadCardActivity() {
        WaitUtils.waitTime(5000)

        onView(withId(R.id.insert_card_instruction_container))
    }
}