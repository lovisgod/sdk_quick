package com.interswitchng.interswitchpossdk.modules.paycode

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R

class PayCodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_code)
    }

    override fun retryTransaction() {

    }
}
