package com.interswitchng.smartpos.activities

import android.content.Intent
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.activities.TransactionResultActivity
import com.interswitchng.smartpos.shared.activities.TransactionResultActivity.Companion.KEY_TRANSACTION_RESULT
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.printslips.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TransactionResultActivityTests {

    private lateinit var activity: TransactionResultActivity

    @Before
    fun setup() {

        val paymentInfo = PaymentInfo(2000, "005609")
        val str = ""
        val result = TransactionResult(PaymentType.Card, str, str, str, TransactionType.Purchase, str, str, str, str, str, str, str, str, str)
        val intent = Intent().apply {
            putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
            putExtra(TransactionResultActivity.KEY_TRANSACTION_RESULT, result)
        }

        activity = Robolectric.buildActivity(TransactionResultActivity::class.java, intent).create().get()

    }


    @Test
    fun shouldLoadIntent() {
        val result = activity.intent.getParcelableExtra<TransactionResult>(KEY_TRANSACTION_RESULT)

        assertNotNull(result)
    }

}