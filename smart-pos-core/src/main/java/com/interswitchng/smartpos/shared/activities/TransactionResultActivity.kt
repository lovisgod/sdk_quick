package com.interswitchng.smartpos.shared.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_activity_transaction_result.*
import org.koin.android.ext.android.inject

class TransactionResultActivity : BaseActivity() {

    private val store: IKeyValueStore by inject()
    private val alert by lazy {
        DialogUtils.getAlertDialog(this)
                .setTitle("An Error Occurred")
                .setMessage("Would you like to try another payment method?")
    }

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
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult()
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
            if (result.responseCode != IsoUtils.TIMEOUT_CODE && result.responseCode != IsoUtils.OK) {
                printSlip(this, UserType.Customer)
            }
        }

        if (result.responseCode != IsoUtils.OK) showAlert()
        // show alert notification
        else showNotification()
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

            // print slip
            printSlip?.apply {
                if (!hasPrintedCustomerCopy) printSlip(this, UserType.Customer)
                else printSlip(this, UserType.Merchant)
            }
        }

        closeBtn.setOnClickListener {
            setResult()
            finish()
        }
    }


    private fun showNotification() {
        Alerter.clearCurrent(this)
        val isSuccessful = result.responseCode == IsoUtils.OK
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
                .setDuration(2 * 1000)
                .show()
    }

    private fun setResult() {
        val purchaseResult = PurchaseResult(result.responseCode, result.responseMessage, result.stan)
        val intent = IswPos.setResult(Intent(), purchaseResult)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun showAlert() {
        alert
                .setPositiveButton(R.string.isw_action_change_payment) { dialog, _ ->
                    dialog.dismiss()
                    showPaymentOptions()
                }
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ ->
                    setResult()
                    finish()
                    dialog.dismiss()
                }
                .show()
    }


    private fun printSlip(slip: TransactionSlip, userType: UserType) {

        // get printer status
        val printStatus = posDevice.printer.canPrint()

        // print based on status
        when (printStatus) {
            is Error -> toast(printStatus.message)
            else -> {
                printBtn.isEnabled = false
                printBtn.isClickable = false

                val disposable = ThreadUtils.createExecutor {
                    val status = posDevice.printer.printSlip(slip.getSlipItems(), userType)

                    runOnUiThread {
                        Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                        printBtn.isEnabled = true
                        printBtn.isClickable = false
                    }

                    hasPrintedCustomerCopy = userType == UserType.Customer
                    hasPrintedMerchantCopy = userType == UserType.Merchant
                }

                disposables.add(disposable)
            }
        }
    }

    override fun getTransactionResult(transaction: Transaction): TransactionResult? = null

    companion object {
        const val KEY_TRANSACTION_RESULT = "transaction_result_key"
    }
}
