package com.interswitchng.smartpos

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.interswitchng.smartpos.di.activityModules
import com.interswitchng.smartpos.di.appModules
import com.interswitchng.smartpos.modules.card.CardActivity
import com.interswitchng.smartpos.modules.home.HomeActivity
import com.interswitchng.smartpos.modules.paycode.PayCodeActivity
import com.interswitchng.smartpos.modules.settings.SettingsActivity
import com.interswitchng.smartpos.modules.ussdqr.QrCodeActivity
import com.interswitchng.smartpos.modules.ussdqr.UssdActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.errors.NotConfiguredException
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.inject
import java.util.*


class IswPos private constructor(private val app: Application, internal val device: POSDevice, internal val config: POSConfig) {

    @Throws(NotConfiguredException::class)
    fun initiatePayment(activity: Activity, amount: Int, paymentType: PaymentType?) {

        if (!isConfigured()) throw NotConfiguredException()

        val stan = getNextStan()

        // getResult the activity class object
        val typeClass = when (paymentType) {
            PaymentType.PayCode -> PayCodeActivity::class.java
            PaymentType.QR -> QrCodeActivity::class.java
            PaymentType.USSD -> UssdActivity::class.java
            PaymentType.Card -> CardActivity::class.java
            else ->  HomeActivity::class.java
        }
        // create payment info for purchase request
        val paymentInfo = PaymentInfo(amount, stan)
        // create intent with payment info and flags
        val intent = Intent(app, typeClass).apply {
            putExtra(Constants.KEY_PAYMENT_INFO, paymentInfo)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        // start activity
        activity.startActivityForResult(intent, CODE_PURCHASE)
    }

    companion object {
        // code used to start purchase request
        const val CODE_PURCHASE: Int = 23849
        // purchase result information
        private const val KEY_PURCHASE_RESULT = "purchase_result_key"

        private object Container: KoinComponent
        private const val KEY_STAN = "stan"
        private lateinit var INSTANCE: IswPos
        private var isSetup = false
        private val store: IKeyValueStore by Container.inject()


        internal fun isConfigured () = TerminalInfo.get(store) != null

        @JvmStatic
        fun configureTerminal(app: Application, device: POSDevice, config: POSConfig) {

            if (!isSetup) {

                // prevent multiple threads from creating instance
                synchronized(this) {
                    INSTANCE = IswPos(app, device, config)
                }

                // add app context
                val appContext = module(override = true) {
                    single { app.applicationContext }
                    single { device }
                }

                // set up koin
                val modules = listOf(appContext) + appModules + activityModules
                loadKoinModules(modules)

                // setup usb connector if exists
                config.usbConnector?.configure(app)

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

        @JvmStatic
        fun getResult(data: Intent): PurchaseResult = data.getParcelableExtra(KEY_PURCHASE_RESULT)

        internal fun setResult(data: Intent, result: PurchaseResult): Intent = data.putExtra(KEY_PURCHASE_RESULT, result)
    }
}