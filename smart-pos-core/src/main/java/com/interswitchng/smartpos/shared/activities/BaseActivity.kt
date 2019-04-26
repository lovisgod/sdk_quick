package com.interswitchng.smartpos.shared.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.interfaces.library.TransactionRequeryCallback
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.models.utils.IswCompositeDisposable
import com.interswitchng.smartpos.shared.models.utils.IswDisposable
import com.interswitchng.smartpos.shared.utilities.CurrencyUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_content_amount.*
import kotlinx.android.synthetic.main.isw_content_toolbar.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    data class PollingText(val title: String, val subTitle: String)

    internal val posDevice: POSDevice by inject()
    private val payableService: HttpService by inject()
    private val alert by lazy {
        DialogUtils.getAlertDialog(this)
                .setTitle("Cancel Transaction")
                .setMessage("Are you sure you want to cancel current transaction?")
                .setNegativeButton(R.string.isw_no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.isw_yes) { dialog, _ -> dialog.dismiss(); finish(); }
    }

    protected val iswPos: IswPos by inject()
    protected val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }
    protected val disposables = IswCompositeDisposable()
    private lateinit var pollingText: PollingText


    private lateinit var transactionResponse: Transaction
    private var pollingExecutor: IswDisposable? = null
    private var isPolling = false
    private lateinit var transactionStatus: TransactionStatus

    // getResult payment info
    internal lateinit var paymentInfo: PaymentInfo
    private val timer by lazy { Timer() }


    override fun onDestroy() {
        super.onDestroy()
        // stop network polling
        stopPolling()
        // dispose all threads
        disposables.dispose()

        // process results
        if (::transactionResponse.isInitialized) {
            getTransactionResult(transactionResponse)?.apply {
                val purchaseResult = PurchaseResult(responseCode, responseMessage, paymentType, stan)
                val intent = IswPos.setResult(Intent(), purchaseResult)
                // set result as ok with result intent
                setResult(Activity.RESULT_OK, intent)

            } ?: setResult(Activity.RESULT_CANCELED) // else set result as cancelled
        } else setResult(Activity.RESULT_CANCELED) // else set result as cancelled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
        pollingText = getPollingText()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.isw_menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.cancelPayment -> {
                alert.show()
                true
            }
            R.id.changePaymentMethod -> {
                val exclude = when (this) {
                    is QrCodeActivity -> BottomSheetOptionsDialog.QR
                    is PayCodeActivity -> BottomSheetOptionsDialog.PAYCODE
                    is CardActivity -> BottomSheetOptionsDialog.CARD
                    is UssdActivity -> BottomSheetOptionsDialog.USSD
                    else -> BottomSheetOptionsDialog.NONE
                }

                return showPaymentOptions(exclude)
            }
            else -> false
        }
    }

    protected fun showPaymentOptions(exclude: String = BottomSheetOptionsDialog.NONE): Boolean {
        val info: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
        val optionsDialog: BottomSheetOptionsDialog = BottomSheetOptionsDialog.newInstance(exclude, info)
        optionsDialog.show(supportFragmentManager, optionsDialog.tag)
        return true
    }

    override fun onResume() {
        super.onResume()
        setSupportActionBar(toolbar)
        if (this !is TransactionResultActivity) {
            val currency = CurrencyUtils.getCurrencySymbol(terminalInfo.currencyCode)
            currencySymbol.text = currency
        }
    }


    protected fun startPolling(status: TransactionStatus) {
        transactionStatus = status
        schedulePolling()
    }

    // for Qr and USSD only
    protected fun checkTransactionStatus(status: TransactionStatus) {
        if (isPolling) stopPolling()

        isPolling = false

        // show progress alert
        showProgressAlert()
        // check payment status and timeout after 5 minutes
        val seconds = resources.getInteger(R.integer.iswPollingTime)
        val paymentType = when (this) {
            is QrCodeActivity -> PaymentType.QR
            else -> PaymentType.USSD
        }
        pollingExecutor = payableService.checkPayment(paymentType, status, seconds.toLong(), TransactionStatusCallback())
    }

    private fun schedulePolling() {
        timer.scheduleAtFixedRate(object : TimerTask() {

            override fun run() = runOnUiThread {
                if (::transactionStatus.isInitialized)
                    checkTransactionStatus(transactionStatus)
            }

        }, 10_000, 35_000)


    }

    protected fun showProgressAlert(canCancel: Boolean = true) {
        Alerter.create(this)
                .setTitle(pollingText.title)
                .setText(pollingText.subTitle)
                .enableProgress(true)
                .setDismissable(false)
                .enableInfiniteDuration(true)
                .setBackgroundColorRes(R.color.iswColorPrimaryDark)
                .setProgressColorRes(android.R.color.white).also {
                    // add cancel  button if cancel is allowed
                    if (canCancel) it.addButton("Cancel", R.style.AlertButton, View.OnClickListener {
                        Toast.makeText(this, "Status check stopped", Toast.LENGTH_LONG).show()
                        Alerter.clearCurrent(this)
                        onCheckStopped()
                        Handler().postDelayed(::stopPolling, 300)
                    })
                }
                .show()
    }

    private fun completePayment() {
        Alerter.clearCurrent(this)
        showTransactionResult(transactionResponse)
    }

    private fun stopPolling() {
        Alerter.clearCurrent(this)
        pollingExecutor?.dispose()
        timer.cancel()
        isPolling = false
    }

    internal fun showTransactionResult(transaction: Transaction) {
        val result = getTransactionResult(transaction)
        // only show result activity if result is non-null
        result?.let {
            val resultIntent = Intent(this, TransactionResultActivity::class.java)
                    .putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
                    .putExtra(TransactionResultActivity.KEY_TRANSACTION_RESULT, it)
                    .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)

            startActivity(resultIntent)
            finish()
        }
    }


    protected fun toast(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }
    }

    internal abstract fun getTransactionResult(transaction: Transaction): TransactionResult?

    override fun onBackPressed() {
        // do nothing
    }

    open fun onCheckStopped() {
        // do nothing
    }

    open fun getPollingText(): PollingText {
        val isCodeActivity = this is QrCodeActivity || this is UssdActivity

        val title =
                if (isCodeActivity) getString(R.string.isw_title_confirmation_in_progress)
                else getString(R.string.isw_title_transaction_in_progress)

        val subTitle =
                if (isCodeActivity) getString(R.string.isw_sub_title_checking_transaction_status)
                else getString(R.string.isw_sub_title_processing_progress)

        return PollingText(title, subTitle)
    }

    // class that provides implementation for transaction status callbacks
    private inner class TransactionStatusCallback : TransactionRequeryCallback {


        override fun onTransactionCompleted(transaction: Transaction) = runOnUiThread {
            isPolling = false
            // set and complete payment
            transactionResponse = transaction
            completePayment()
        }

        override fun onTransactionStillPending(transaction: Transaction) {
            isPolling = true
            // extend the time for the notification
        }

        override fun onTransactionError(transaction: Transaction?, throwable: Throwable?) = runOnUiThread {
            isPolling = false

            // getResult error message
            val message = throwable?.message
                    ?: transaction?.responseDescription
                    ?: "An error occurred, please try again"


            // clear current notification
            Alerter.clearCurrent(this@BaseActivity)

            // change notification to error notification
            Alerter.create(this@BaseActivity)
                    .setTitle(getString(R.string.isw_title_transaction_error))
                    .setText(message)
                    .setIcon(R.drawable.isw_ic_error)
                    .setDismissable(true)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(android.R.color.holo_red_dark)
                    .setDuration(15 * 1000)
                    .show()

            onCheckStopped()
        }

        override fun onTransactionTimeOut() = runOnUiThread {
            isPolling = false
            // change notification to error notification

            // clear current notification
            Alerter.clearCurrent(this@BaseActivity)

            // change notification to error notification
            Alerter.create(this@BaseActivity)
                    .setTitle(getString(R.string.isw_title_transaction_timeout))
                    .setText(getString(R.string.isw_content_transaction_in_progress_time_out))
                    .setIcon(R.drawable.isw_ic_warning)
                    .setDismissable(true)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(android.R.color.holo_orange_dark)
                    .setDuration(15 * 1000)
                    .show()

            onCheckStopped()
        }

    }

}