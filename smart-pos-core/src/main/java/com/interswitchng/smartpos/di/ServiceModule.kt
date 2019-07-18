package com.interswitchng.smartpos.di

import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.*
import com.interswitchng.smartpos.shared.interfaces.library.HttpService
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.IsoSocket
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.UserStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.services.EmailServiceImpl
import com.interswitchng.smartpos.shared.services.HttpServiceImpl
import com.interswitchng.smartpos.shared.services.UserStoreImpl
import com.interswitchng.smartpos.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.smartpos.shared.services.iso8583.tcp.IsoSocketImpl
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.services.storage.KeyValueStoreImpl
import com.interswitchng.smartpos.shared.services.storage.SharePreferenceManager
import com.interswitchng.smartpos.shared.services.storage.TransactionLogServiceImpl
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.zhuinden.monarchy.Monarchy
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module

internal val serviceModule = module {
    single { IswPos.getInstance() }
    single<HttpService> { HttpServiceImpl(get()) }
    single<UserStore> { UserStoreImpl(get()) }
    single { SharePreferenceManager(androidContext()) }
    single<KeyValueStore> { KeyValueStoreImpl(get()) }
    single<EmailService> { EmailServiceImpl(get()) }
    factory<IsoService> { IsoServiceImpl(androidContext(), get(), get()) }

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
    factory<IsoSocket> {
        val resource = androidContext().resources
        val serverIp = resource.getString(R.string.isw_nibss_ip)
        val port = resource.getInteger(R.integer.iswNibssPort)
        // try getting terminal info
        val store: KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        // getResult timeout based on terminal info
        val timeout = terminalInfo?.serverTimeoutInSec ?: resource.getInteger(R.integer.iswTimeout)

        return@factory IsoSocketImpl(serverIp, port, timeout * 1000)
    }
}
