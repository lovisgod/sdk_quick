package com.interswitchng.interswitchpossdk.mockservices

import android.support.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.response.Bank
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.utilities.SimpleResponseHandler

internal class MockPayableService : Payable {

    private val code = "00"
    private val desc = "Successful"
    private val date = "2018-11-23T16:21:22"
    private val ref = "46524"
    private val bankShortCode = "*737*6*46524#"
    private val defaultShortCode = "*322*419*051691201*46524#"
    private val terminalId = "2069018M"
    private val transactionReference = "TRSuiKz3mpqoXmDsW7bqiiZsZ"
    private val qrCodeData = "https://www.google.com"
    private val response = CodeResponse(code, desc, date, ref, bankShortCode, defaultShortCode, terminalId, transactionReference, qrCodeData)

    private val banksResponse = listOf(Bank(107, "ACCESS BANK PLC", "ABP"),
            Bank(108, "ECOBANK NIGERIA", "ECO"),
            Bank(103, "GUARANTY TRUST BANK", "GTB"),
            Bank(114, "Skye Bank", "SKYE"))

    override fun initiateUssdPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>) {

        Thread(Runnable {

            Thread.sleep(1000)

            runOnUiThread { callback(response, null) }

        }).start()
    }

    override fun initiateQrPayment(request: CodeRequest, callback: SimpleResponseHandler<CodeResponse?>) {

        Thread(Runnable {

            Thread.sleep(1000)

            runOnUiThread { callback(response, null) }

        }).start()
    }

    override fun getBanks(callback: SimpleResponseHandler<List<Bank>?>) {
        Thread(Runnable {
            Thread.sleep(2000)

            runOnUiThread { callback(banksResponse, null) }
        }).start()
    }

}