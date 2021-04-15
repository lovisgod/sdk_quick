package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.billPayment.repository.billRepo
import com.interswitchng.smartpos.modules.main.transfer.repo.RealmRepo
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.interfaces.library.*
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.posconfig.PosType
import com.interswitchng.smartpos.shared.services.EmailServiceImpl
import com.interswitchng.smartpos.shared.services.HttpServiceImpl
import com.interswitchng.smartpos.shared.services.SaturnServiceImpl
import com.interswitchng.smartpos.shared.services.UserStoreImpl
import com.interswitchng.smartpos.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.smartpos.shared.services.iso8583.tcp.IsoSocketImpl
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.kimono.KimonoHttpServiceImpl
import com.interswitchng.smartpos.shared.services.storage.KeyValueStoreImpl
import com.interswitchng.smartpos.shared.services.storage.SharePreferenceManager
import com.interswitchng.smartpos.shared.services.storage.TransactionLogServiceImpl
import com.zhuinden.monarchy.Monarchy
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

internal val serviceModule = module() //override = true
{
    single { IswPos.getInstance() }
    single<HttpService> { HttpServiceImpl(get()) }

    single<SaturnService> { SaturnServiceImpl(get()) }

    factory<IsoService> { (isKimono: Boolean) ->


        if (isKimono) {
            return@factory KimonoHttpServiceImpl(androidContext(), get(), get(), get(), get(), get(), get())
        } else {
            return@factory IsoServiceImpl(androidContext(), get(), get(), get())
        }
    }



    single<UserStore> { UserStoreImpl(get()) }
    single { SharePreferenceManager(androidContext()) }
    single<KeyValueStore> { KeyValueStoreImpl(get()) }
    single<EmailService> { EmailServiceImpl(get()) }

    single<billRepo> { billRepo() }





    single<TransactionLogService> {

        val realmKey = IsoUtils.hexToBytes(BuildConfig.REALM_KEY)
        // create realm configuration
        val realmConfig = RealmConfiguration.Builder()
                .encryptionKey(realmKey)
                .build()

        // build monarchy
        val monarchy = Monarchy.Builder()
                .setRealmConfiguration(realmConfig)
                .build()

        TransactionLogServiceImpl(monarchy)
    }

    // realm repository
    single<RealmRepo> { RealmRepo(get()) }


    factory<IsoSocket> {
        val resource = androidContext().resources

        // try getting terminal info
        val store: KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        // getResult timeout based on terminal info
        val timeout = terminalInfo?.serverTimeoutInSec ?: resource.getInteger(R.integer.iswTimeout)

        val serverIp = terminalInfo?.serverIp ?: Constants.ISW_TERMINAL_IP
        val port = terminalInfo?.serverPort ?: BuildConfig.ISW_TERMINAL_PORT
        return@factory IsoSocketImpl(serverIp, port, timeout * 1000)
    }
}
