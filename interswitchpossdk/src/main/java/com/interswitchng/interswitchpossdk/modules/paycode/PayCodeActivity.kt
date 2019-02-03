package com.interswitchng.interswitchpossdk.modules.paycode

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import kotlinx.android.synthetic.main.activity_pay_code.*
import kotlinx.android.synthetic.main.content_toolbar.*
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
        val amount = NumberFormat.getInstance().format(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)
    }

    override fun onTransactionSuccessful(transaction: Transaction) {

    }

}
