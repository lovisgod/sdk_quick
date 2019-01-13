package com.interswitchng.interswitchpossdk.shared.utilities

import com.interswitchng.interswitchpossdk.shared.interfaces.PayableResponseHandler
import retrofit2.*
import java.io.IOException
import java.lang.reflect.Type

internal class Simple<R>(private val call: Call<R>) {

    fun test(responseHandler: PayableResponseHandler<R?>) {
        // run in the same thread
        try {
            val response = call.execute()
            responseHandler(response.body(), null)
        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun processPayment(responseHandler: PayableResponseHandler<R?> ) {

        // define callback
        val callback = object : Callback<R> {
            override fun onFailure(call: Call<R>?, t: Throwable?) {
                responseHandler(null, t)
            }

            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                responseHandler(response?.body(), null)
            }

        }

        // enqueue network call
        call.enqueue(callback)
    }
}


internal class PayableCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Any> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Any = Simple(call)
}