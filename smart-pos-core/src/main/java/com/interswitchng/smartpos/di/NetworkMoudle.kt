package com.interswitchng.smartpos.di

import android.util.Base64
import com.igweze.ebi.simplecalladapter.SimpleCallAdapterFactory
import com.interswitchng.smartpos.BuildConfig
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.interfaces.library.UserStore
import com.interswitchng.smartpos.shared.interfaces.retrofit.IAuthService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IEmailService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IHttpService
import com.interswitchng.smartpos.shared.interfaces.retrofit.IKimonoHttpService
import com.interswitchng.smartpos.shared.utilities.ToStringConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import retrofit2.SimpleXmlConverterFactory


const val AUTH_INTERCEPTOR = "auth_interceptor"
const val RETROFIT_EMAIL = "email_retrofit"
const val RETROFIT_PAYMENT = "payment_retrofit"
const val RETROFIT_KIMONO = "kimono_retrofit"

internal val networkModule = module {

    factory {
        OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
    }

    // retrofit interceptor for authentication
    single(AUTH_INTERCEPTOR) {
        val userManager: UserStore = get()
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

    // retrofit email
    single(RETROFIT_EMAIL) {
        val sendGridUrl = androidContext().getString(R.string.isw_email_end_point)
        val builder = Retrofit.Builder()
                .baseUrl(sendGridUrl)
                .addConverterFactory(GsonConverterFactory.create())

        // okhttp client for retrofit
        val clientBuilder: OkHttpClient.Builder = get()
        //  add auth interceptor for sendGrid
        clientBuilder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/json")
                    .addHeader("Authorization", "Bearer ${BuildConfig.ISW_EMAIL_API_KEY}")
                    .build()

            chain.proceed(request)
        }

        // build and add client to retrofit
        val client = clientBuilder.build()
        builder.client(client)

        return@single builder.build()
    }




    single(RETROFIT_KIMONO) {


        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val kimonoServiceUrl = "https://qa.interswitchng.com/"
        val builder = Retrofit.Builder()
                .addConverterFactory(SimpleXmlConverterFactory.create())
              .addConverterFactory(ToStringConverterFactory())
//                .addConverterFactory(GsonConverterFactory.create())...
                .baseUrl(kimonoServiceUrl)
                .addCallAdapterFactory(SimpleCallAdapterFactory.create())


        val clientBuilder: OkHttpClient.Builder = get()
        //  add auth interceptor for sendGrid
        clientBuilder
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                    .addHeader("Content-type", "application/xml")

                    .build()

            chain.proceed(request)
        }

        // build and add client to retrofit
        val client = clientBuilder.build()
        builder.client(client)

        return@single builder.build()
    }




    // retrofit isw payment
    single(RETROFIT_PAYMENT) {
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

    // create Email service with retrofit
    single {
        val retrofit: Retrofit = get(RETROFIT_EMAIL)
        return@single retrofit.create(IEmailService::class.java)
    }

    // create payment Http service with retrofit
    single {
        val retrofit: Retrofit = get(RETROFIT_PAYMENT)
        return@single retrofit.create(IHttpService::class.java)
    }



    //create retrofit Kimono Http service with retrofit
    single {
        val retrofit: Retrofit = get(RETROFIT_KIMONO)
        return@single retrofit.create(IKimonoHttpService::class.java)
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