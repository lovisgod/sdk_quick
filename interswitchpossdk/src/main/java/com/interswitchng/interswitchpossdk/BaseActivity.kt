package com.interswitchng.interswitchpossdk

import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.TransactionRequeryCallback
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.tapadoo.alerter.Alerter
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    private val payableService: Payable by inject()
    protected val instance: IswPos by inject()
    private lateinit var transactionResponse: Transaction


    fun checkTransactionStatus(status: TransactionStatus) {
        Alerter.create(this)
                .setTitle(getString(R.string.title_transaction_in_progress))
                .setText(getString(R.string.title_checking_transaction_status))
                .enableProgress(true)
                .setDismissable(false)
                .enableInfiniteDuration(true)
                .setBackgroundColorRes(android.R.color.darker_gray)
                .setProgressColorRes(android.R.color.white)
                .show()

        // check payment status and timeout after 5 minutes
        val seconds = resources.getInteger(R.integer.poolingTime)
        payableService.checkPayment(status, seconds.toLong(), TransactionStatusCallback())
    }

    private fun completePayment() {
        Alerter.clearCurrent(this)

        Alerter.create(this)
                .setTitle(getString(R.string.title_transaction_successful))
                .setText(getString(R.string.title_transaction_completed_successfully))
                .setDismissable(false)
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(15 * 1000)
                .show()

        onTransactionSuccessful(transactionResponse)
        Toast.makeText(this, "Transaction completed successfully", Toast.LENGTH_LONG).show()
    }

    protected fun printReciept() {
        val msg = "You have printed receipt"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }


    internal abstract fun onTransactionSuccessful(transaction: Transaction)


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
            // get error message
            val message = throwable?.message
                    ?: transaction?.responseDescription
                    ?: "An error occurred, please try again"


            // clear current notification
            Alerter.clearCurrent(this@BaseActivity)

            // change notification to error notification
            Alerter.create(this@BaseActivity)
                    .setTitle(getString(R.string.title_transaction_error))
                    .setText(message)
                    .setIcon(R.drawable.ic_error)
                    .setDismissable(false)
                    .setBackgroundColorRes(android.R.color.holo_red_dark)
                    .setDuration(15 * 1000)
                    .show()
        }

        override fun onTransactionTimeOut() = runOnUiThread {
            // change notification to error notification

            // clear current notification
            Alerter.clearCurrent(this@BaseActivity)

            // change notification to error notification
            Alerter.create(this@BaseActivity)
                    .setTitle(getString(R.string.title_transaction_timeout))
                    .setText(getString(R.string.content_transaction_in_progress_time_out))
                    .setIcon(R.drawable.ic_warning)
                    .setDismissable(false)
                    .setBackgroundColorRes(android.R.color.holo_orange_dark)
                    .setDuration(15 * 1000)
                    .show()
        }

    }
}