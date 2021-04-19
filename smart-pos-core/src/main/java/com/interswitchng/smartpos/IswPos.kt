package com.interswitchng.smartpos

import android.app.Application
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.interswitchng.smartpos.di.networkModule
import com.interswitchng.smartpos.di.serviceModule
import com.interswitchng.smartpos.modules.main.MainActivity

import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.errors.NotConfiguredException
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.device.POSFingerprint
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.interswitchng.smartpos.shared.models.core.PurchaseResult
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.zhuinden.monarchy.Monarchy
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.inject
import java.util.*


/**
 * This class is the primary bridge used to interact with the payment SDK.
 * It is responsible for triggering the purchase request and presenting the
 * final result of the triggered transaction
 *
 */
class IswPos private constructor(private val app: Application, internal val device: POSDevice, internal val config: POSConfig) {

    companion object {
        // code used to start purchase request
        const val CODE_PURCHASE: Int = 23849
        // purchase result information
        private const val KEY_PURCHASE_RESULT = "purchase_result_key"

        private object Container: KoinComponent
        private const val KEY_STAN = "stan"
        private const val HAS_FINGERPRINT = "fingerprint_support"
        private lateinit var INSTANCE: IswPos
        private var isSetup = false
        private val store: KeyValueStore by Container.inject()


        /**
         * This function determines if the sdk has been configured
         */
        internal fun isConfigured () = TerminalInfo.get(store) != null


        /**
         * This method is responsible for setting up the terminal for the current application
         */
        @JvmStatic
        fun setupTerminal(
            app: Application,
            device: POSDevice,
            fingerPrint: POSFingerprint?,
            config: POSConfig,
            withRealm: Boolean,
            supportsFingerprintAuthorization: Boolean = false
        ) {
            if (!isSetup) {

                // prevent multiple threads from creating iswPos
                synchronized(this) {
                    INSTANCE = IswPos(app, device, config)
                }

                // add app context
                val appContext = module(override = true) {
                    single { app.applicationContext }
                    single { device }
                    if (fingerPrint != null) {
                        single<POSFingerprint> { fingerPrint }
                    }
                }

                // set up koin
                val modules = listOf(appContext, serviceModule, networkModule)
                loadKoinModules(modules)

                // setup monarchy and realmdb
                Monarchy.init(app)

                // setup usb connector if exists
                config.usbConnector?.configure(app)

                // setup monarchy and realmdb
                if (withRealm) Monarchy.init(app)

                //Set if device has fingerprint
                store.saveBoolean(HAS_FINGERPRINT, supportsFingerprintAuthorization)

                // set setup flag
                isSetup = true
            }
        }


        /**
         * This method returns the next STAN (System Trace Audit Number)
         */
        internal fun getNextStan(): String {
            var stan = store.getNumber(KEY_STAN, 0)

            // compute and save new stan
            val newStan = if (stan > 999999) 0 else ++stan
            store.saveNumber(KEY_STAN, newStan)

            return String.format(Locale.getDefault(), "%06d", newStan)
        }



        /**
         * This method returns the single instance of IswPos for the current app
         */
        @JvmStatic
        fun getInstance(): IswPos = INSTANCE

        /**
         * This method extracts the final transaction result
         */
        @JvmStatic
        fun getResult(data: Intent): PurchaseResult = data.getParcelableExtra(KEY_PURCHASE_RESULT)

        /**
         * This method sets the out going transaction result for the triggered purchase transaction requeset
         */
        internal fun setResult(data: Intent, result: PurchaseResult): Intent = data.putExtra(KEY_PURCHASE_RESULT, result)
    }
}