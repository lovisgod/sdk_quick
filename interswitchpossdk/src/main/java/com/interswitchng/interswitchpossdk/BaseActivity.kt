package com.interswitchng.interswitchpossdk

import android.support.v7.app.AppCompatActivity
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import org.koin.android.ext.android.inject

abstract class BaseActivity: AppCompatActivity() {

    private val payableService: Payable by inject()
    protected val instance: IswPos by inject()


    protected fun checkTransactionStatus() {
        // TODO perform long polling
        var hasresponse = false
        val thread = Thread(Runnable {
            var secs = 0L
            while (!hasresponse) {
                secs += 2
                // TODO make synchronous request
                Thread.sleep(secs)
            }
        })

        thread.start()
    }

    private fun printReciept() {
        // TODO perform printing
    }

    abstract fun retryTransaction()

}