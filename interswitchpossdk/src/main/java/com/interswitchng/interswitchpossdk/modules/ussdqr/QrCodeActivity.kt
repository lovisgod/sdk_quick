package com.interswitchng.interswitchpossdk.modules.ussdqr


import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest.Companion.QR_FORMAT_RAW
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest.Companion.TRANSACTION_QR
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.activity_qr_code.*
import org.koin.android.ext.android.inject


class QrCodeActivity : BaseActivity() {

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
        val request = CodeRequest.from(instance.config, paymentInfo, TRANSACTION_QR, QR_FORMAT_RAW)

        dialog.show()

        if (qrBitmap != null) {
            qrCodeImage.setImageBitmap(qrBitmap)
            dialog.dismiss()
        } else {
            // initiate qr payment
            paymentService.initiateQrPayment(request) { response, throwable ->
                if (throwable != null) handleError(throwable)
                else response?.apply { handleResponse(request, this) }
            }
        }

    }

    private fun handleResponse(request: CodeRequest, response: CodeResponse) {
        when(response.responseCode) {
            CodeResponse.OK -> {
                qrBitmap = response.getBitmap(this)
                val byte = qrBitmap?.byteCount
                runOnUiThread {
                    qrCodeImage.setImageBitmap(qrBitmap)
                    dialog.dismiss()
                    checkTransactionStatus(TransactionStatus(response.transactionReference!!, instance.config.merchantCode))
                }
            }
            CodeResponse.SERVER_ERROR -> {
                runOnUiThread {
                    val msg = "An error occured: ${response.responseDescription}"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            else -> {
                // show error message
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        // handle error
    }

    override fun onTransactionSuccessful(transaction: Transaction) {
        printBtn.visibility = View.VISIBLE
        textMessage.visibility = View.VISIBLE

        printBtn.setOnClickListener {
            printBtn.isClickable = false
            printBtn.isEnabled = false

            Toast.makeText(this, "Printing Receipt", Toast.LENGTH_LONG).show()
        }

        qrCodeImage.setImageResource(R.drawable.placeholder)
        Toast.makeText(this, "amount of ${transaction.amount} paid successfully", Toast.LENGTH_LONG).show()
    }
}
