package com.interswitchng.interswitchpossdk.adapter

import com.interswitchng.interswitchpossdk.Utilities.getJson
import com.interswitchng.interswitchpossdk.shared.interfaces.IHttpService
import com.interswitchng.interswitchpossdk.shared.utilities.Simple
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleAdapterFactory
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleCallAdapter
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.koin.standalone.get
import org.koin.test.KoinTest
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SimpleAdapterTest: KoinTest {


    @Test
    fun `should return response when successful`() {
        val msg = "some response"

        val response: Response<String> = mock {
            on(mock.body()) doReturn msg
        }

        val call: Call<String> = mock {
            on(mock.execute()) doReturn response
        }

        val simpleAdapter = SimpleCallAdapter<String>(String::class.java)
        val simpleResponse = simpleAdapter.adapt(call) as Simple<String>

        simpleResponse.test { s, throwable ->
            assertNull("error is not null", throwable)
            assertNotNull("response is null", s)
            assertSame("error message is different", msg, s)
        }

    }

    @Test
    fun `should return error when exception happens`() {
        val msg = "some error occurred"
        val error: IOException = mock {
            on(mock.message) doReturn msg
            on(mock.localizedMessage) doReturn msg
        }

        val call: Call<String> = mock {
            on(mock.execute()) doThrow error
        }

        val simpleAdapter = SimpleCallAdapter<String>(String::class.java)
        val simpleResponse = simpleAdapter.adapt(call) as Simple<String>

        simpleResponse.test { s, throwable ->
            assertNotNull("error is null", throwable)
            assertSame("error message is different", msg, throwable?.message)
            assertNull("response is not null", s)
        }

    }
}