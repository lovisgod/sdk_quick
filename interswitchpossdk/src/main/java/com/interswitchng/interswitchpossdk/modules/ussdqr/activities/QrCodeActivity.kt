package com.interswitchng.interswitchpossdk.modules.ussdqr.activities


import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.activity_qr_code.*
import org.koin.android.ext.android.inject


class QrCodeActivity : AppCompatActivity() {

    private val paymentService: Payable by inject()

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }

    private var qrBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
    }

    override fun onStart() {
        super.onStart()
        setupImage()
    }


    private fun setupImage() {

        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)

        dialog.show()

        Thread(Runnable {

            Thread.sleep(1000)

            runOnUiThread {

                if (qrBitmap != null) {
                    qrCodeImage.setImageBitmap(qrBitmap)
                    dialog.dismiss()
                } else {

                    // initiate qr payment
            //        paymentService.initiateQrPayment(paymentInfo) { response, throwable ->
            //            if (throwable != null) {
            //                // handle error
            //            } else {
            //                // handle response, show
            //            }
            //        }

                    val url = "https://www.google.com"
                    val empty = ""

                    val response = CodeResponse(empty, empty, empty, empty, empty, empty, empty, empty, url)
                    qrBitmap = response.getBitmap(this)

                    qrCodeImage.setImageBitmap(qrBitmap)
                    dialog.dismiss()
                }
            }
        }).start()

    }

}
