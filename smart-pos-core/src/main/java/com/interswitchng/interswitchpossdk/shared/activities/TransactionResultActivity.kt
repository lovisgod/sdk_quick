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
    private var hasPrintedMerchantCopy = false
    private var hasPrintedCustomerCopy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_transaction_result)

        result = intent.getParcelableExtra(KEY_TRANSACTION_RESULT)

        // setup UI
        setupUI()
        // show alert notification
        showNotification()
    }

    private fun setupUI() {

        printSlip = TerminalInfo.get(store)?.let { result.getSlip(this, it) }

        // set amount text view
        val amountStr = DisplayUtils.getAmountString(paymentInfo.amount / 100)
        amountText.text = getString(R.string.isw_currency_amount, amountStr)

        // setup transaction status
        setupTransactionStatus(result)

        // setup buttons
        setupButtons()

        // print user's copy slip
        printSlip?.apply {
            if (result.responseCode != IsoUtils.TIMEOUT_CODE) {
                posDevice.printer.printSlip(getSlipItems(), UserType.Customer)
                // set flag to true
                hasPrintedCustomerCopy = true
            }
        }
    }

    private fun setupTransactionStatus(result: TransactionResult) {
        // check if transaction is successful
        val isSuccessful = result.responseCode == IsoUtils.OK

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

        // set payment text colors
        val statusColorInt = if (isSuccessful) R.color.iswTextColorSecondaryDark else android.R.color.white
        val statusColor = ContextCompat.getColor(this, statusColorInt)
        paymentStatus.setTextColor(statusColor)
        amountText.setTextColor(statusColor)

        // set payment status container bg
        val colorInt = if (isSuccessful) R.color.iswTextColorSecondaryLight else R.color.iswTextColorError
        paymentStatusContainer.setBackgroundColor(ContextCompat.getColor(this, colorInt))

    }

    private fun setupButtons() {

        printBtn.setOnClickListener {
            printBtn.isClickable = false
            printBtn.isEnabled = false

            // print slip
            printSlip?.apply {
                if (!hasPrintedCustomerCopy) {
                    posDevice.printer.printSlip(getSlipItems(), UserType.Customer)
                    // set flag to true
                    hasPrintedCustomerCopy = true
                } else {
                    posDevice.printer.printSlip(getSlipItems(), UserType.Merchant)
                    // set flag to true
                    hasPrintedMerchantCopy = true
                }

            }
            Toast.makeText(this, "Printing Receipt", Toast.LENGTH_LONG).show()
            printBtn.isClickable = true
            printBtn.isEnabled = true
        }

        closeBtn.setOnClickListener { finish() }
    }


    private fun showNotification() {
        Alerter.clearCurrent(this)
        val isSuccessful = result.responseCode == "00"
        val title =
                if (isSuccessful) R.string.isw_title_transaction_successful
                else R.string.isw_title_transaction_failed

        val text =
                if (isSuccessful) R.string.isw_title_transaction_completed_successfully
                else R.string.isw_title_transaction_error

        val background =
                if (isSuccessful) R.color.iswTextColorSuccess
                else R.color.iswTextColorError

        Alerter.create(this)
                .setTitle(getString(title))
                .setText(getString(text))
                .setDismissable(true)
                .setBackgroundColorRes(background)
                .setDuration(3 * 1000)
                .show()
    }

    override fun getTransactionResult(transaction: Transaction): TransactionResult? = null

    companion object {
        const val KEY_TRANSACTION_RESULT = "transaction_result_key"
    }
}
