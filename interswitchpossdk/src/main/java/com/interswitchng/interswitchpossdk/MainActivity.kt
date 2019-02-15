package com.interswitchng.interswitchpossdk

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.content_payment_options.*
import kotlinx.android.synthetic.main.content_toolbar.*



class MainActivity : AppCompatActivity() {

    private lateinit var optionsDialog: BottomSheetOptionsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.title = "Payment Method"

        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)
        // set up the UI
        optionsDialog = BottomSheetOptionsDialog.newInstance(info = paymentInfo)
        cardPayment.setOnClickListener { optionsDialog.show(supportFragmentManager, optionsDialog.tag) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else super.onOptionsItemSelected(item)

    }
}

