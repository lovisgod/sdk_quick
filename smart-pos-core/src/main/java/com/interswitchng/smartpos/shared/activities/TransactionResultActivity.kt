package com.interswitchng.smartpos.shared.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.slips.TransactionSlip
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import com.interswitchng.smartpos.shared.utilities.toast
import com.interswitchng.smartpos.shared.viewmodel.TransactionResultViewModel
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_activity_transaction_result.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * This activity displays final transaction status to the user
 */
class TransactionResultActivity : AppCompatActivity() {

    private val store: KeyValueStore by inject()
    private val resultViewModel: TransactionResultViewModel by viewModel()

    private lateinit var result: TransactionResult
    private lateinit var paymentInfo: PaymentInfo

    private val terminalInfo by lazy {  TerminalInfo.get(store) }
    private var printSlip: TransactionSlip? = null
    private var hasPrintedMerchantCopy = false
    private var hasPrintedCustomerCopy = false
    private var hasSentEmail = false
    private val alert by lazy {

        val title = IsoUtils.getIsoResult(result.responseCode)?.second

        return@lazy DialogUtils.getAlertDialog(this)
                .setTitle(title)
                .setMessage("Would you like to try another payment method?")
                .setPositiveButton(R.string.isw_action_change_payment) { dialog, _ ->
                    dialog.dismiss()
                    val exclude = when (result.paymentType) {
                        PaymentType.Card -> BottomSheetOptionsDialog.CARD
                        PaymentType.QR -> BottomSheetOptionsDialog.QR
                        PaymentType.PayCode -> BottomSheetOptionsDialog.PAYCODE
                        PaymentType.USSD -> BottomSheetOptionsDialog.USSD
                    }
                    showPaymentOptions(exclude)
                }
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ ->
                    setResult()
                    finish()
                    dialog.dismiss()
                }
    }

    private val emailSendingDialog by lazy {
        val dialog = DialogUtils.getLoadingDialog(this)
        dialog.setTitle("Sending Email")
        dialog.setMessage("Sending...")
        return@lazy dialog
    }

    private val emailInputDialog by lazy {
        DialogUtils.getEmailInputDialog(this) { email ->
            // handle user interaction here
            when(email){
                null -> toast("Email cancelled") // user cancelled dialog
                else -> {
                    // process email
                    terminalInfo?.let {
                        resultViewModel.sendMail(email, result, it)
                        // prevent warning dialog from showing
                        hasPrintedCustomerCopy = true
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_transaction_result)

        paymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
        result = intent.getParcelableExtra(KEY_TRANSACTION_RESULT)

        // setup UI
        setupUI()

        // log transaction result
        resultViewModel.logTransaction(result)
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult()
    }

    private fun setupUI() {

        printSlip = terminalInfo?.let { result.getSlip(it) }

        // set amount text view
        val amountStr = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_currency_amount, amountStr)

        // setup transaction status
        setupTransactionStatus(result)

        // observe viewModel
        observeViewModel()

        // setup buttons
        setupButtons()

        // print user's copy slip
        printSlip?.let {
            if (result.responseCode != IsoUtils.TIMEOUT_CODE && result.responseCode != IsoUtils.OK) {
                resultViewModel.printSlip(UserType.Customer, it)
            }
        }

        if (result.responseCode != IsoUtils.OK) alert.show()
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
        val imageIcon = if (isSuccessful) R.drawable.isw_ic_check else R.drawable.isw_ic_error
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

    private fun observeViewModel() {

        with(resultViewModel) {
            val owner = { lifecycle }

            // observe printer message
            printerMessage.observe(owner) {
                it?.let { msg -> toast(msg) }
            }

            // observe customer print
            printedCustomerCopy.observe(owner) {
                hasPrintedCustomerCopy = it ?: false
            }

            // observe merchant print
            printedMerchantCopy.observe(owner) {
                hasPrintedMerchantCopy = it ?: false
            }

            // observe print button function
            printButton.observe(owner) { isEnabled ->
                printBtn.isEnabled = isEnabled ?: false
                printBtn.isClickable = isEnabled ?: false
            }

            // observe email sent result
            emailStatus.observe(owner) {
                it?.apply {
                    hasSentEmail = this
                    val msg =
                            if (this) "Email has been sent Successfully"
                            else "An error occurred sending Email"

                    toast(msg)
                }
            }

            // observe email dialog
            emailDialog.observe(owner) {
                it?.let { show ->
                    if (show) emailSendingDialog.show()
                    else emailSendingDialog.hide()
                }
            }
        }
    }

    private fun setupButtons() {

        printBtn.setOnClickListener {

            // print slip
            printSlip?.let {
                if (!hasPrintedCustomerCopy) resultViewModel.printSlip(UserType.Customer, it)
                else if (hasPrintedMerchantCopy) resultViewModel.printSlip(UserType.Merchant, it, reprint = true)
                else {
                    // if has not printed merchant copy
                    // print merchant copy
                    resultViewModel.printSlip(UserType.Merchant, it)
                    // change print text to re-print
                    printBtn.text = getString(R.string.isw_title_re_print_receipt)
                }
            }
        }

        closeBtn.setOnClickListener {

            val hasNotPrinted = !hasPrintedCustomerCopy && !hasPrintedCustomerCopy
            if (hasNotPrinted && !hasSentEmail) {
                DialogUtils.getAlertDialog(this)
                .setTitle("Close without printing?")
                .setMessage("Are you sure you want to close without printing")
                .setNegativeButton(android.R.string.no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(android.R.string.yes) { dialog, _ -> dialog.dismiss(); setResult(); finish(); }
                .show()
            } else {
                setResult()
                finish()
            }
        }

        btnEReceipt.setOnClickListener {
            emailInputDialog.show()
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
                .enableSwipeToDismiss()
                .setBackgroundColorRes(background)
                .setDuration(2 * 1000)
                .show()
    }


    private fun showPaymentOptions(exclude: String = BottomSheetOptionsDialog.NONE): Boolean {
        val info: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
        val optionsDialog: BottomSheetOptionsDialog = BottomSheetOptionsDialog.newInstance(exclude, info)
        optionsDialog.show(supportFragmentManager, optionsDialog.tag)
        return true
    }

    private fun setResult() {
        val purchaseResult = PurchaseResult(result.responseCode, result.responseMessage, result.paymentType, result.cardType, result.stan)
        val intent = IswPos.setResult(Intent(), purchaseResult)
        setResult(Activity.RESULT_OK, intent)
    }

    companion object {
        const val KEY_TRANSACTION_RESULT = "transaction_result_key"
    }
}
