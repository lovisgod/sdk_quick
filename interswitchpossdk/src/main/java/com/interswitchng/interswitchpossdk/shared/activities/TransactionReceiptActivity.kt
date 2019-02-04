package com.interswitchng.interswitchpossdk.shared.activities

import android.os.Bundle
import android.widget.Toast
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_transaction_receipt.*

class TransactionReceiptActivity : BaseActivity() {

    private val printSlip = mutableListOf<PrintObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_receipt)

        // setup buttons
        setupButtons()

        // show success notification
        showSuccessNotification()
    }

    private fun setupButtons() {
        val transaction = intent.getIntExtra(KEY_TRANSACTION_AMOUNT, 0)
        val amount = PrintObject.Data(transaction.toString())
        printSlip.add(amount)

        printBtn.setOnClickListener {
            posDevice.printReceipt(printSlip)
            printBtn.isClickable = false
            printBtn.isEnabled = false

            Toast.makeText(this, "Printing Receipt", Toast.LENGTH_LONG).show()
            printBtn.isClickable = true
            printBtn.isEnabled = true
        }

        closeBtn.setOnClickListener { finish() }
    }


    private fun showSuccessNotification() {
        Alerter.clearCurrent(this)

        Alerter.create(this)
                .setTitle(getString(R.string.title_transaction_successful))
                .setText(getString(R.string.title_transaction_completed_successfully))
                .setDismissable(false)
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(3 * 1000)
                .show()
    }

    companion object {
        val KEY_TRANSACTION_AMOUNT = "transaction_amount"
    }
}
