package com.interswitchng.interswitchpossdk.shared.activities

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.core.UserType
import com.interswitchng.interswitchpossdk.shared.models.printslips.slips.TransactionSlip
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_activity_transaction_result.*
import org.koin.android.ext.android.inject

class TransactionResultActivity : BaseActivity() {

    private val store: IKeyValueStore by inject()
    private var printSlip: TransactionSlip? = null
    private lateinit var result: TransactionResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_transaction_result)

        result = intent.getParcelableExtra(KEY_TRANSACTION_RESULT)

        // setup UI
        setupUI()
        // show success notification
        showSuccessNotification()
    }

    private fun setupUI() {

        printSlip = TerminalInfo.get(store)?.let { result.getSlip(it) }

        // set amount text view
        val amountStr = DisplayUtils.getAmountString(paymentInfo.amount)
        amountText.text = getString(R.string.isw_currency_amount, amountStr)

        // setup transaction status
        setupTransactionStatus(result)

        // setup buttons
        setupButtons()
    }

    private fun setupTransactionStatus(result: TransactionResult) {
        // check if transaction is successful
        val isSuccessful = result.responseCode == "00"

        // set transaction status message
        val transactionStatus =
                if (isSuccessful) getString(R.string.isw_title_transaction_successful)
                else IsoUtils.getIsoResult(result.responseCode)?.second

        transactionResult.text = transactionStatus

        // set transaction status icon
        val imageIcon = if (isSuccessful) R.drawable.ic_check else R.drawable.ic_error
        val imageColor = if (isSuccessful) android.R.color.white else R.color.iswTextColorError
        statusImage.setImageResource(imageIcon)
        statusImage.setColorFilter(ContextCompat.getColor(this, imageColor))

        // set payment status
        val status = if (isSuccessful) R.string.isw_title_paid else R.string.isw_title_failed
        paymentStatus.text = getString(status)

        // set payment status container bg
        val colorInt = if (isSuccessful) R.color.iswTextColorSecondaryLight else R.color.iswTextColorError
        paymentStatusContainer.setBackgroundColor(ContextCompat.getColor(this, colorInt))

    }

    private fun setupButtons() {

        printBtn.setOnClickListener {
            printBtn.isClickable = false
            printBtn.isEnabled = false

            // print slip
            printSlip?.apply { posDevice.printer.printSlip(getSlipItems(), UserType.Customer) }
            Toast.makeText(this, "Printing Receipt", Toast.LENGTH_LONG).show()
            printBtn.isClickable = true
            printBtn.isEnabled = true
        }

        closeBtn.setOnClickListener { finish() }
    }


    private fun showSuccessNotification() {
        Alerter.clearCurrent(this)

        Alerter.create(this)
                .setTitle(getString(R.string.isw_title_transaction_successful))
                .setText(getString(R.string.isw_title_transaction_completed_successfully))
                .setDismissable(true)
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(3 * 1000)
                .show()
    }

    override fun getTransactionResult(transaction: Transaction): TransactionResult? = null

    companion object {
        const val KEY_TRANSACTION_RESULT = "transaction_result_key"
    }
}
