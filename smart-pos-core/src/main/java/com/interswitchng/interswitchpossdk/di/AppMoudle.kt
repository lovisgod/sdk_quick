package com.interswitchng.interswitchpossdk.di

import android.widget.Toast
import com.interswitchng.interswitchpossdk.BuildConfig
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.interfaces.*
import com.interswitchng.interswitchpossdk.shared.interfaces.device.EmvCardTransaction
import com.interswitchng.interswitchpossdk.shared.interfaces.device.IPrinter
import com.interswitchng.interswitchpossdk.shared.interfaces.device.POSDevice
import com.interswitchng.interswitchpossdk.shared.interfaces.library.*
import com.interswitchng.interswitchpossdk.shared.interfaces.network.IHttpService
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.core.UserType
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.interswitchpossdk.shared.services.PayableService
import com.interswitchng.interswitchpossdk.shared.services.storage.SharePreferenceManager
import com.interswitchng.interswitchpossdk.shared.services.UserService
import com.interswitchng.interswitchpossdk.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.interswitchpossdk.shared.services.iso8583.tcp.NibssIsoSocket
import com.interswitchng.interswitchpossdk.shared.services.storage.KeyValueStore
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val AUTH_INTERCEPTOR = "auth_interceptor"

private val serviceModule = module {
    single { IswPos.getInstance() }
    single<Payable> { PayableService(get()) }
    single<IUserService> { UserService() }
    single { SharePreferenceManager(androidContext()) }
    single<IKeyValueStore> { KeyValueStore(get()) }
    factory<IsoService> { IsoServiceImpl(androidContext(), get(), get()) }
    factory<IsoSocket> {
        val resource = androidContext().resources
        val serverIp = resource.getString(R.string.nibss_ip)
        val port = resource.getInteger(R.integer.iswNibssPort)
        // try getting terminal info
        val store: IKeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        // get timeout based on terminal info
        val timeout = terminalInfo?.serverTimeoutInSec ?: resource.getInteger(R.integer.iswTimeout)

        return@factory NibssIsoSocket(serverIp, port, timeout * 1000)
    }

    // TODO remove this
    if (BuildConfig.DEBUG && BuildConfig.MOCK) {
        single<POSDevice>(override = true) {
            object : POSDevice {
                override val printer: IPrinter
                    get() = object : IPrinter {
                        override fun printSlip(slip: List<PrintObject>, user: UserType) {
                            val context = androidContext()
                            Toast.makeText(context, "Printing Slip", Toast.LENGTH_LONG).show()
                        }
                    }

                override fun getEmvCardTransaction(): EmvCardTransaction {
                    return object : EmvCardTransaction {
                        override fun setEmvCallback(callback: EmvCallback) {}
                        override fun removeEmvCallback(callback: EmvCallback) {}
                        override fun setupTransaction(amount: Int, terminalInfo: TerminalInfo) {}
                        override fun startTransaction(): EmvResult = EmvResult.OFFLINE_APPROVED
                        override fun getCardDetail(): CardDetail = CardDetail("", "")
                        override fun completeTransaction() {}
                        override fun cancelTransaction() {}
                        override fun getTransactionInfo(): EmvData? = null
                    }
                }
            }
        }
    }
}

private val networkModule = module {

    factory {
        OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
    }

    // retrofit interceptor for authentication
    single(AUTH_INTERCEPTOR) {
        val userManager: IUserService = get()
        return@single Interceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/json")
                    .addHeader("Authorization", "Bearer ${userManager.getToken()}")
                    .build()

            return@Interceptor chain.proceed(request)
        }
    }

    // retrofit
    single {
        val iswBaseUrl = androidContext().getString(R.string.ISW_USSD_QR_BASE_URL)
        val builder = Retrofit.Builder()
                .baseUrl(iswBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleAdapterFactory.create())

        // get the okhttp client for the retrofit
        val clientBuilder: OkHttpClient.Builder = get()

        // get auth interceptor for client
        val authInterceptor: Interceptor = get(AUTH_INTERCEPTOR)
        // add auth interceptor for max services
        clientBuilder.addInterceptor(authInterceptor)

        // add client to retrofit builder
        val client = clientBuilder.build()
        builder.client(client)

        return@single builder.build()
    }

    // create Http service with retrofit
    single {
        val retrofit: Retrofit = get()
        return@single retrofit.create(IHttpService::class.java)
    }

    // TODO remove dependency
    single {
        val retrofit: Retrofit = get()
        return@single retrofit.create(PaymentInitiator::class.java)
    }

}


internal val appModules = listOf(serviceModule, networkModule)