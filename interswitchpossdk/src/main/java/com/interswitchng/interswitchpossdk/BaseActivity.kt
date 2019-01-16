package com.interswitchng.interswitchpossdk

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.tapadoo.alerter.Alerter
import org.koin.android.ext.android.inject

abstract class BaseActivity : AppCompatActivity() {

    private val payableService: Payable by inject()
    protected val instance: IswPos by inject()


    fun checkTransactionStatus(transaction: TransactionStatus) {
        val alerter = Alerter.create(this)
                .setTitle("Transaction in progress")
                .setText("Checking transaction status...")
                .enableProgress(true)
                .setDismissable(false)
                .setDuration(3 * 1000)
                .setBackgroundColorRes(android.R.color.darker_gray)
                .setProgressColorRes(android.R.color.white)

        alerter.show()

        alerter.setDuration(10 * 1000)


        var hasResponse = false
        Thread(Runnable {
            var secs = 0L
            while (!hasResponse) {
                secs += 2
                hasResponse = secs > 5
                // TODO perform long polling
                // TODO make synchronous request
//                payableService.checkPayment(transaction) { transaction, throwable ->
//                    if (throwable != null) {
//                        // handle errior
//                    } else if (transaction?.isCompleted() == true) {
//
//                    }
//                }

                Thread.sleep(secs * 1000)
            }

            // complete payment on ui thread
            runOnUiThread { completePayment() }
        }).start()
    }

    private fun completePayment() {
        Alerter.clearCurrent(this)
        Alerter.create(this)
                .setTitle("Transaction Successful")
                .setText("Transaction completed successfully")
                .setBackgroundColorRes(android.R.color.holo_green_light)
                .setDuration(15 * 1000)
                .show()

        Toast.makeText(this, "Transaction completed successfully", Toast.LENGTH_LONG).show()
    }

    protected fun printReciept() {
        val msg = "You have printed receipt"
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    abstract fun retryTransaction()

}