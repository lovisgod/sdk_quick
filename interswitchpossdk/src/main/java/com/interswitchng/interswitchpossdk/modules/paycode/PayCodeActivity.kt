package com.interswitchng.interswitchpossdk.modules.paycode

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction

class PayCodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_code)
    }

    override fun onTransactionSuccessful(transaction: Transaction) {

    }

}
