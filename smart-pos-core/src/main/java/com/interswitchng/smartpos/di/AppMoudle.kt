package com.interswitchng.smartpos.di

import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.*
import com.interswitchng.smartpos.shared.interfaces.retrofit.IAuthService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IHttpService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.services.HttpServiceImpl
import com.interswitchng.smartpos.shared.services.storage.SharePreferenceManager
import com.interswitchng.smartpos.shared.services.UserStoreImpl
import com.interswitchng.smartpos.shared.services.iso8583.IsoServiceImpl
import com.interswitchng.smartpos.shared.services.iso8583.tcp.IsoSocketImpl
import com.interswitchng.smartpos.shared.services.storage.KeyValueStoreImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.util.Base64

const val AUTH_INTERCEPTOR = "auth_interceptor"

private val serviceModule = module {
    single { IswPos.getInstance() }
    single<HttpService> { HttpServiceImpl(get()) }
    single<UserStore> { UserStoreImpl(get()) }
    single { SharePreferenceManager(androidContext()) }
    single<com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore> { KeyValueStoreImpl(get()) }
    factory<IsoService> { IsoServiceImpl(androidContext(), get(), get()) }
    factory<IsoSocket> {
        val resource = androidContext().resources
        val serverIp = resource.getString(R.string.isw_nibss_ip)
        val port = resource.getInteger(R.integer.iswNibssPort)
        // try getting terminal info
        val store: com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore = get()
        val terminalInfo = TerminalInfo.get(store)
        // getResult timeout based on terminal info
        val timeout = terminalInfo?.serverTimeoutInSec ?: resource.getInteger(R.integer.iswTimeout)

        return@factory IsoSocketImpl(serverIp, port, timeout * 1000)
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
        val userManager: UserStoreImpl = get()
        return@single Interceptor { chain ->
            return@Interceptor userManager.getToken {
                val request = chain.request().newBuilder()
                        .addHeader("Content-type", "application/json")
                        .addHeader("Authorization", "Bearer $it")
                        .build()

                return@getToken chain.proceed(request)
            }
        }
    }

    // retrofit
    single {
        val iswBaseUrl = androidContext().getString(R.string.ISW_USSD_QR_BASE_URL)
        val builder = Retrofit.Builder()
                .baseUrl(iswBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())

        // getResult the okhttp client for the retrofit
        val clientBuilder: OkHttpClient.Builder = get()

        // getResult auth interceptor for client
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

    // create Auth service with retrofit
    single {
        val iswBaseUrl = androidContext().getString(R.string.ISW_TOKEN_BASE_URL)
        val builder = Retrofit.Builder()
                .baseUrl(iswBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())

        // getResult the okhttp client for the retrofit
        val clientBuilder: OkHttpClient.Builder = get()

        // get credentials encoding from pos config
        val iswPos: IswPos = get()
        val credentials = "${iswPos.config.clientId}:${iswPos.config.clientSecret}"
        val encoding = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        // add auth interceptor for max services
        clientBuilder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Basic $encoding")
                    .build()

            return@addInterceptor chain.proceed(request)
        }

        // add client to retrofit builder
        val client = clientBuilder.build()
        builder.client(client)


        val retrofit: Retrofit = builder.build()
        return@single retrofit.create(IAuthService::class.java)
    }

}


internal val appModules = listOf(serviceModule, networkModule)