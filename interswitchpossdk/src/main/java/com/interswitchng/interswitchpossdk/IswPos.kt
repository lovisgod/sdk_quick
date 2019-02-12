package com.interswitchng.interswitchpossdk

import android.app.Application
import android.content.Intent
import com.interswitchng.interswitchpossdk.di.activityModules
import com.interswitchng.interswitchpossdk.di.appModules
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.posconfig.POSConfiguration
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules



class IswPos private constructor(private val app: Application, internal val device: POSDevice) {

    internal val config: POSConfiguration

    init {
        // set terminal parameters
        // TODO setup asynchronously
        val alias = "000007"
        val terminalId = "2069018M"
        val merchantId = "IBP000000001384"
        val merchantLocation = "AIRTEL NETWORKS LIMITED PH MALL"
        // String currencyCode = "566";
        // String posGeoCode = "0023400000000056";
        val terminalType = "PAX"
        val uniqueId = "280-820-589"
        val merchantCode = "MX5882"
        config = POSConfiguration(alias, terminalId, merchantId, terminalType, uniqueId, merchantLocation, merchantCode)
    }

    fun initiatePayment(amount: Int) {
        val stan = "005609"
        val paymentInfo = PaymentInfo(amount, stan)
        val intent = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
        app.startActivity(intent)
    }

    companion object {

        private var isConfigured = false

        private lateinit var INSTANCE: IswPos

        @JvmStatic
        fun configureTerminal(app: Application, device: POSDevice) {

            if (!isConfigured) {

                // prevent multiple threads from creating instance
                synchronized(this) {
                    INSTANCE = IswPos(app, device)
                }

                // add app context
                val appContext = module {
                    single { app.applicationContext }
                    single { device }
                }

                // set up koin
                val modules = appModules + activityModules + appContext
                loadKoinModules(modules)

                // set configured
                isConfigured = true
            }
        }

        private fun getTerminalParameters() {

        }

        @JvmStatic
        fun getInstance(): IswPos = INSTANCE

    }
}