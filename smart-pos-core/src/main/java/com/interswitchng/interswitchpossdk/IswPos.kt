package com.interswitchng.interswitchpossdk

import android.app.Application
import android.content.Intent
import com.interswitchng.interswitchpossdk.di.activityModules
import com.interswitchng.interswitchpossdk.di.appModules
import com.interswitchng.interswitchpossdk.modules.home.HomeActivity
import com.interswitchng.interswitchpossdk.modules.settings.SettingsActivity
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.errors.NotConfiguredException
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.models.transaction.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.inject
import java.util.*


class IswPos private constructor(private val app: Application, internal val device: POSDevice) {

    internal val merchantCode = "MX5882"

    @Throws(NotConfiguredException::class)
    fun initiatePayment(amount: Int) {

        if (!isConfigured()) throw NotConfiguredException()

        val stan = getNextStan()
        val paymentInfo = PaymentInfo(amount, stan)
        val intent = Intent(app, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
        app.startActivity(intent)


    }


    companion object: KoinComponent {
        private const val KEY_STAN = "stan"
        private lateinit var INSTANCE: IswPos
        private var isSetup = false


        private val store: IKeyValueStore by inject()

        internal fun isConfigured () = TerminalInfo.get(store) != null

        @JvmStatic
        fun configureTerminal(app: Application, device: POSDevice) {

            if (!isSetup) {

                // prevent multiple threads from creating instance
                synchronized(this) {
                    INSTANCE = IswPos(app, device)
                }

                // add app context
                val appContext = module(override = true) {
                    single { app.applicationContext }
                    single { device }
                }

                // set up koin
                val modules = listOf(appContext) + appModules + activityModules
                loadKoinModules(modules)

                // set setup flag
                isSetup = true
            }
        }


        internal fun getNextStan(): String {
            var stan = store.getNumber(KEY_STAN, 0)

            val newStan = if (stan > 999999) 0 else ++stan
            store.saveNumber(KEY_STAN, newStan)

            return String.format(Locale.getDefault(), "%06d", newStan)
        }


        @JvmStatic
        fun showSettingsScreen() {
            val app = INSTANCE.app
            val intent = Intent(app, SettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            app.startActivity(intent)
        }

        @JvmStatic
        fun getInstance(): IswPos = INSTANCE

    }
}