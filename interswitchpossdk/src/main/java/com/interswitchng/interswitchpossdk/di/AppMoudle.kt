package com.interswitchng.interswitchpossdk.di

import com.interswitch.posinterface.posshim.CardService
import com.interswitch.posinterface.posshim.PosInterface
import com.interswitchng.interswitchpossdk.BuildConfig
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.interfaces.IUserService
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.services.PayableService
import com.interswitchng.interswitchpossdk.shared.services.SharePreferenceManager
import com.interswitchng.interswitchpossdk.shared.services.UserService
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
    single<Payable>  { PayableService(get()) }
    single<IUserService> { UserService() }
    single { SharePreferenceManager(androidContext()) }
    single { CardService.getInstance(androidContext()) }
    single { PosInterface.getInstance(androidContext(), get()) }
}

private val networkModule = module {

    factory {
        OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
    }

    // retrofit interceptor for authentication
    single(AUTH_INTERCEPTOR) {
        val userManager: IUserService = get()
        return@single Interceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/json" )
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


}


internal val appModules = listOf(serviceModule, networkModule)