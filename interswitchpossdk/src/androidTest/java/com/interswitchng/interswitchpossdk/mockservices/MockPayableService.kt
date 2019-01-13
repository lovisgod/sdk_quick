package com.interswitchng.interswitchpossdk.mockservices

import android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PayableResponseHandler
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse

internal class MockPayableService : Payable {

    private val url = "https://www.google.com"
    private val empty = ""
    private val response = CodeResponse(empty, empty, empty, empty, empty, empty, empty, empty, url)


    override fun initiateUssdPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>) {

        Thread(Runnable {

            Thread.sleep(1000)

            runOnUiThread { callback(response, null) }

        }).start()
    }

    override fun initiateQrPayment(request: CodeRequest, callback: PayableResponseHandler<CodeResponse?>) {

        Thread(Runnable {

            Thread.sleep(1000)

            runOnUiThread { callback(response, null) }

        }).start()
    }

}