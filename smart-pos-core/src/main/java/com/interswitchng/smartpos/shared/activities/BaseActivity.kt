package com.interswitchng.smartpos.shared.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import com.interswitchng.smartpos.shared.interfaces.network.TransactionRequeryCallback
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_content_toolbar.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import java.util.concurrent.ExecutorService

abstract class BaseActivity : AppCompatActivity() {

    internal val posDevice: POSDevice by inject()
    private val payableService: Payable by inject()

    protected val instance: IswPos by inject()
    protected val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }


    private lateinit var transactionResponse: Transaction
    private var pollingExecutor: ExecutorService? = null

    // getResult payment info
    internal lateinit var paymentInfo: PaymentInfo


    override fun onDestroy() {
        super.onDestroy()
        stopPolling()

        if (::transactionResponse.isInitialized) {
            val result = getTransactionResult(transactionResponse)?.let {
                val purchaseResult = PurchaseResult(it.responseCode, it.responseMessage, it.stan)
                val intent = IswPos.setResult(Intent(), purchaseResult)
                // set result as ok with result intent
                setResult(Activity.RESULT_OK, intent)

            } ?: setResult(Activity.RESULT_CANCELED) // else set result as cancelled
        }
        else setResult(Activity.RESULT_CANCELED) // else set result as cancelled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.isw_menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.cancelPayment -> {
                // TODO show confirmation dialog
                finish()
                true
            }
            R.id.changePaymentMethod -> {
                val exclude = when(this) {
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

    private fun showPaymentOptions(exclude: String): Boolean {
        val info: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
        val optionsDialog: BottomSheetOptionsDialog = BottomSheetOptionsDialog.newInstance(exclude, info)
        optionsDialog.show(supportFragmentManager, optionsDialog.tag)
        return true
    }

    override fun onResume() {
        super.onResume()
        setSupportActionBar(toolbar)
    }


    // for Qr and USSD only
    fun checkTransactionStatus(status: TransactionStatus) {
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

    protected fun showProgressAlert() {
        Alerter.create(this)
                .setTitle(getString(R.string.isw_title_transaction_in_progress))
                .setText(getString(R.string.isw_title_checking_transaction_status))
                .enableProgress(true)
                .setDismissable(false)
                .enableInfiniteDuration(true)
                .setBackgroundColorRes(android.R.color.darker_gray)
                .setProgressColorRes(android.R.color.white)
                .show()
    }

    private fun completePayment() {
        Alerter.clearCurrent(this)
        showTransactionResult(transactionResponse)
    }

    protected fun stopPolling() {
        Alerter.clearCurrent(this)
        pollingExecutor?.shutdownNow()
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

    open fun onCheckError() {
        // do nothing
    }

    // class that provides implementation for transaction status callbacks
    private inner class TransactionStatusCallback: TransactionRequeryCallback {


        override fun onTransactionCompleted(transaction: Transaction) = runOnUiThread {
            // set and complete payment
            transactionResponse = transaction
            completePayment()
        }

        override fun onTransactionStillPending(transaction: Transaction) {
            // extend the time for the notification
        }

        override fun onTransactionError(transaction: Transaction?, throwable: Throwable?) = runOnUiThread {
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
                    .setIcon(R.drawable.ic_error)
                    .setDismissable(true)
                    .setBackgroundColorRes(android.R.color.holo_red_dark)
                    .setDuration(15 * 1000)
                    .show()

            onCheckError()
        }

        override fun onTransactionTimeOut() = runOnUiThread {
            // change notification to error notification

            // clear current notification
            Alerter.clearCurrent(this@BaseActivity)

            // change notification to error notification
            Alerter.create(this@BaseActivity)
                    .setTitle(getString(R.string.isw_title_transaction_timeout))
                    .setText(getString(R.string.isw_content_transaction_in_progress_time_out))
                    .setIcon(R.drawable.ic_warning)
                    .setDismissable(true)
                    .setBackgroundColorRes(android.R.color.holo_orange_dark)
                    .setDuration(15 * 1000)
                    .show()

            onCheckError()
        }

    }

}