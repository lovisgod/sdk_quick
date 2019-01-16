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
        val alerter = Alerter.create(this)
                .setTitle(getString(R.string.title_transaction_in_progress))
                .setText(getString(R.string.title_checking_transaction_status))
                .enableProgress(true)
                .setDismissable(false)
                .setDuration(10 * 1000)
                .setBackgroundColorRes(android.R.color.darker_gray)
                .setProgressColorRes(android.R.color.white)

        alerter.show()

        payableService.checkPayment(status, 5 * 60 * 1000, TransactionStatusCallback())
    }

    private fun completePayment() {
        Alerter.clearCurrent(this)
        Alerter.create(this)
                .setTitle(getString(R.string.title_transaction_successful))
                .setText(getString(R.string.title_transaction_completed_successfully))
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


        override fun onTransactionCompleted(transaction: Transaction) {
            transactionResponse = transaction
            // complete payment on ui thread
            runOnUiThread { completePayment() }
        }

        override fun onTransactionStillPending(transaction: Transaction) {
            // extend the time for the notification
        }

        override fun onTransactionError(transaction: Transaction?, throwable: Throwable?) {
            // change notification to error notification
        }

        override fun onTransactionTimeOut() {
            // change notification to error notification
        }

    }
}