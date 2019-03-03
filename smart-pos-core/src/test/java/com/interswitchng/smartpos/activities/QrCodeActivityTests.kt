package com.interswitchng.smartpos.activities

import android.content.Intent
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.modules.ussdqr.activities.QrCodeActivity
import com.interswitchng.smartpos.shared.Constants
import com.interswitchng.smartpos.shared.interfaces.library.Callback
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast


@RunWith(RobolectricTestRunner::class)
class QrCodeActivityTests {

    private lateinit var activity: QrCodeActivity
    private val parcel = PaymentInfo(2000, "stan")
    private val qrIntent = Intent().apply {
        putExtra(Constants.KEY_PAYMENT_INFO, parcel)
    }

    private lateinit var instance: IswPos

    @Before
    fun setup() {
        instance = mock()
    }

    @Test
    fun should_show_show_error_message_when_qr_code_request_fails() {
        // mock dependencies
        val failedResponse: CodeResponse = mock()
        whenever(failedResponse.responseCode).thenReturn(CodeResponse.SERVER_ERROR)

        val service: Payable = mock()
        whenever(service.initiateQrPayment(any(), any())).then{
            val callback: Callback<CodeResponse?>  = it.getArgument(1)
            callback(failedResponse, null)
        }

        // load mock dependencies
        loadKoinModules(module {
            single { service }
            single { instance }
        })

        activity = Robolectric.buildActivity(QrCodeActivity::class.java, qrIntent).create().start().get()

        // check that the last toast was error
        val lastToastText = ShadowToast.getTextOfLatestToast()
        assertNotNull("Last Toast message was null", lastToastText)
        assertTrue(lastToastText.contains("error"))
    }

}