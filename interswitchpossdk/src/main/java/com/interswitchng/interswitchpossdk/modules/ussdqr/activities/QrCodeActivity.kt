package com.interswitchng.interswitchpossdk.modules.ussdqr.activities


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import kotlinx.android.synthetic.main.activity_qr_code.*
import org.koin.android.ext.android.inject


class QrCodeActivity : AppCompatActivity() {

    private val paymentService: Payable by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)


        setupImage()

        // initiate qr payment
//        paymentService.initiateQrPayment(paymentInfo) { response, throwable ->
//            if (throwable != null) {
//                // handle error
//            } else {
//                // handle response, show
//            }
//        }
    }

    private fun setupImage() {
        val url = "https://www.google.com"
        val empty = ""

        val response = CodeResponse(empty, empty, empty, empty, empty, empty, empty, empty, url)
        val bitmap = response.getBitmap(this)
        qrCodeImage.setImageBitmap(bitmap)
    }

}
