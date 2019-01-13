package com.interswitchng.interswitchpossdk

import com.interswitchng.interswitchpossdk.Utilities.getJson
import com.interswitchng.interswitchpossdk.di.appModules
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.utilities.PayableAdapterFactory
import com.nhaarman.mockitokotlin2.mock
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.get
import org.koin.test.KoinTest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PayableAdapterTest: KoinTest {

    private val sampleResponse = getJson("success-code-response.json")
    private val mockServer = MockWebServer()

    @Before
    fun setup() {
        // configure mock server and response
        val url = mockServer.url("/")
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(PayableAdapterFactory.create())
                .baseUrl(url)
                .build()


        val retrofitModule = module {
            single { retrofit.create(IHttpService::class.java) }
        }

        loadKoinModules(retrofitModule)
    }

    @Test
    fun `should extract response from body`() {

        mockServer.enqueue(MockResponse().setBody(sampleResponse))

        // check http service
        val httpService: IHttpService = get()

        // issue request and check results
        httpService.getQrCode(mock()).test { codeResponse, throwable ->
            assertNull("an error occured", throwable)
            assertNotNull("response is null", codeResponse)
        }
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }
}