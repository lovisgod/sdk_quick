package com.interswitchng.interswitchpossdk.modules.paycode

import android.os.Bundle
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import kotlinx.android.synthetic.main.activity_pay_code.*
import java.text.NumberFormat

class PayCodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_code)


        // setup toolbar
        setupToolbar("Pay Code")


        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)
    }

}
