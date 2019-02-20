package com.interswitchng.smartpos.shared.utilities

import retrofit2.*
import java.io.IOException
import java.lang.reflect.Type

internal typealias SimpleResponseHandler<T> = (T, Throwable?) -> Unit

internal class Simple<R>(private val call: Call<R>) {

    fun test(responseHandler: SimpleResponseHandler<R?>) {
        // run in the same thread
        try {
            val response = call.execute()
            responseHandler(response.body(), null)
        } catch (t: IOException) {
            responseHandler(null, t)
        }
    }

    fun process(responseHandler: SimpleResponseHandler<R?> ) {

        // define callback
        val callback = object : Callback<R> {
            override fun onFailure(call: Call<R>?, t: Throwable?) {
                responseHandler(null, t)
            }

            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                val hasError = response?.code() in 400..505
                if (hasError) {
                    val error = response?.errorBody()?.string()
                }
                responseHandler(response?.body(), null)
            }

        }

        // enqueue network call
        call.enqueue(callback)
    }
}


internal class SimpleCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Any> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Any = Simple(call)
}