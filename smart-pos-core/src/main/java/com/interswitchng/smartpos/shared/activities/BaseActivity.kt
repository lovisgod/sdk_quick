package com.interswitchng.smartpos.shared.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.activities.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.utilities.CurrencyUtils
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.toast
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.isw_content_amount.*
import kotlinx.android.synthetic.main.isw_content_toolbar.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject


/**
 * This activity serves as the base class for purchase views and purchase result
 */
abstract class BaseActivity : AppCompatActivity() {

    data class PollingText(val title: String, val subTitle: String)

    internal val posDevice: POSDevice by inject()
    private val alert by lazy {
        DialogUtils.getAlertDialog(this)
                .setTitle("Cancel Transaction")
                .setMessage("Are you sure you want to cancel current transaction?")
                .setNegativeButton(R.string.isw_no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.isw_yes) { dialog, _ -> dialog.dismiss(); finish(); }
    }

    protected val iswPos: IswPos by inject()
    protected val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }
    private lateinit var pollingText: PollingText

    // getResult payment info
    internal lateinit var paymentInfo: PaymentInfo


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
        val currency = CurrencyUtils.getCurrencySymbol(terminalInfo.currencyCode)
        currencySymbol.text = currency
    }

    override fun onDestroy() {
        super.onDestroy()
        // set result as cancelled
        setResult(Activity.RESULT_CANCELED)
    }

    protected fun showProgressAlert(canCancel: Boolean = true, oncancel: () -> Unit = {}) {
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
                        Alerter.clearCurrent(this)
                        toast("Status check stopped")
                        oncancel()
                    })
                }
                .show()
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

        } ?: finish() // else just close activity
    }

    internal abstract fun getTransactionResult(transaction: Transaction): TransactionResult?

    private fun getPollingText(): PollingText {
        val isCodeActivity = this is QrCodeActivity || this is UssdActivity

        val title =
                if (isCodeActivity) getString(R.string.isw_title_confirmation_in_progress)
                else getString(R.string.isw_title_transaction_in_progress)

        val subTitle =
                if (isCodeActivity) getString(R.string.isw_sub_title_checking_transaction_status)
                else getString(R.string.isw_sub_title_processing_progress)

        return PollingText(title, subTitle)
    }

    override fun onBackPressed() {
        // do nothing to prevent user from navigating away from activity
    }

    open fun onCheckStopped() {
        // do nothing
    }

    protected fun runWithInternet(handler: () -> Unit) {
        // ensure that device is connected to internet
        if (!DeviceUtils.isConnectedToInternet(this)) {
            toast("Device is not connected to internet")
            // show no-network dialog
            DialogUtils.getNetworkDialog(this) {
                // trigger handler
                handler()
            }.show()
        } else {
            // trigger handler
            handler()
        }
    }

    internal fun handlePaymentStatus(status: PaymentStatus) {
        // clear current notification
        // if it not pending
        if (status !is PaymentStatus.Pending)
            Alerter.clearCurrent(this)

        // else handle status
        when (status) {

            is PaymentStatus.Complete -> {
                // set and complete payment
                showTransactionResult(status.transaction)
            }

            is PaymentStatus.OngoingTimeout -> {
                // change notification to info notification
                Alerter.create(this@BaseActivity)
                        .setTitle(getString(R.string.isw_title_transaction_timeout))
                        .setText(getString(R.string.isw_content_transaction_in_progress_time_out))
                        .setIcon(R.drawable.isw_ic_warning)
                        .setDismissable(true)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(android.R.color.holo_orange_dark)
                        .setDuration(5 * 1000)
                        .show()
            }

            is PaymentStatus.Timeout -> {
                val title = "Payment not Confirmed"
                val message = "Unable to confirm payment at the moment, please try again later by manually clicking the button below"

                // change notification to error
                Alerter.create(this@BaseActivity)
                        .setTitle(title)
                        .setText(message)
                        .setIcon(R.drawable.isw_ic_warning)
                        .setDismissable(true)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(android.R.color.holo_orange_dark)
                        .setDuration(15 * 1000)
                        .show()

                onCheckStopped()
            }

            is PaymentStatus.Error -> {
                // get alert title
                val title =
                        if (status.errorMsg != null) "Network Error"
                        else getString(R.string.isw_title_transaction_error)

                // getResult error message
                val message = status.errorMsg
                        ?: status.transaction?.responseDescription
                        ?: "An error occurred, please try again"

                // change notification to error notification
                Alerter.create(this@BaseActivity)
                        .setTitle(title)
                        .setText(message)
                        .setIcon(R.drawable.isw_ic_error)
                        .setDismissable(true)
                        .enableSwipeToDismiss()
                        .setBackgroundColorRes(R.color.iswTextColorError)
                        .setDuration(15 * 1000)
                        .show()

                onCheckStopped()
            }
        }
    }
}