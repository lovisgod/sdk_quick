package com.interswitchng.interswitchpossdk

import android.app.Application
import android.content.Context
import android.content.Intent
import com.interswitchng.interswitchpossdk.di.activityModules
import com.interswitchng.interswitchpossdk.di.appModules
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.models.POSConfiguration
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

class IswPos(private val app: Application, internal val config: POSConfiguration) {

    fun initiatePayment(paymentInfo: PaymentInfo) {
        val intent = Intent(app, MainActivity::class.java)
        intent.putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
        app.startActivity(intent)
    }

    companion object {

        private lateinit var INSTANCE: IswPos

        @JvmStatic
        fun configureTerminal(app: Application, configuration: POSConfiguration) {

            // prevent multiple threads from creating instance
            synchronized(this) {
                INSTANCE = IswPos(app, configuration)
            }

            // add app context
            val appContext = module {
                single { app.applicationContext as Context }
            }

            // set up koin
            val modules = appModules + activityModules + appContext
            loadKoinModules(modules)
        }

        @JvmStatic
        fun getInstance(): IswPos = INSTANCE

    }
}