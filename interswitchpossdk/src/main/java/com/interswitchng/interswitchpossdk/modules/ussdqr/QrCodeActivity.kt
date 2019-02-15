package com.interswitchng.interswitchpossdk.modules.ussdqr


import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants.KEY_PAYMENT_INFO
import com.interswitchng.interswitchpossdk.shared.interfaces.library.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentInitiator
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentRequest
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.core.UserType
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.printslips.info.TransactionType
import com.interswitchng.interswitchpossdk.shared.models.transaction.PaymentType
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.CodeRequest.Companion.QR_FORMAT_RAW
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.CodeRequest.Companion.TRANSACTION_QR
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.interswitchng.interswitchpossdk.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.activity_qr_code.*
import kotlinx.android.synthetic.main.content_amount.*
import org.koin.android.ext.android.inject
import java.text.NumberFormat
import java.util.*


class QrCodeActivity : BaseActivity() {

    private val paymentService: Payable by inject()

    // TODO remove reference
    private val initiator: PaymentInitiator by inject()

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val logger by lazy { Logger.with("QR") }

    private var qrBitmap: Bitmap? = null
    private val printSlip = mutableListOf<PrintObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)
    }

    override fun onStart() {
        super.onStart()
        setupImage()
    }


    private fun setupImage() {
        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(KEY_PAYMENT_INFO)

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)
        paymentHint.text = getString(R.string.hint_qr_code)
        changePaymentMethod.setOnClickListener {
            showPaymentOptions(BottomSheetOptionsDialog.QR)
        }


        dialog.show()

        if (qrBitmap != null) {
            qrCodeImage.setImageBitmap(qrBitmap)
            dialog.dismiss()
        } else {
            val request = CodeRequest.from(instance.config, paymentInfo, TRANSACTION_QR, QR_FORMAT_RAW)
            // initiate qr payment
            paymentService.initiateQrPayment(request) { response, throwable ->
                if (throwable != null) handleError(throwable)
                else response?.apply { handleResponse(request, this) }
            }
        }

    }

    private fun showTransactionMocks(request: CodeRequest, response: CodeResponse) {
        mockButtonsContainer.visibility = View.VISIBLE
        // TODO remove mock trigger
        initiateButton.isEnabled = true
        initiateButton.setOnClickListener {
            initiateButton.isEnabled = false
            initiateButton.isClickable = false
            val payment = PaymentRequest(4077131215677, request.amount.toInt(), 566, 623222, response.transactionReference!!)
            initiator.initiateQr(payment).process { s, t ->
                if (t != null) logger.log(t.localizedMessage)
                else logger.log(s!!)
            }

            // check transaction status
            checkTransactionStatus(TransactionStatus(response.transactionReference, instance.config.merchantCode))
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            printCodeButton.isEnabled = false
            printCodeButton.isClickable = false
            posDevice.printer.printSlip(printSlip, UserType.Customer)
            Toast.makeText(this, "Printing Code", Toast.LENGTH_LONG).show()
            printCodeButton.isEnabled = true
            printCodeButton.isClickable = false
        }
    }

    private fun handleResponse(request: CodeRequest, response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {
                qrBitmap = response.getBitmap(this)
                val bitmap = PrintObject.BitMap(qrBitmap!!)
                printSlip.add(bitmap)
                runOnUiThread {
                    qrCodeImage.setImageBitmap(qrBitmap)
                    dialog.dismiss()

                    // TODO remove mock trigger
                    showTransactionMocks(request, response)
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

    override fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()
        val responseMsg = IsoUtils.getIsoResult(transaction.responseCode)?.second
                ?: transaction.responseDescription
                ?: "Error"

        return TransactionResult(
                paymentType = PaymentType.QR,
                dateTime = DisplayUtils.getIsoString(now),
                amount = DisplayUtils.getAmountString(paymentInfo.amount),
                type = TransactionType.Purchase,
                authorizationCode = transaction.responseCode,
                responseMessage = responseMsg,
                responseCode = transaction.responseCode,
                cardPan = "", cardExpiry = "", cardType = "",
                stan = "", pinStatus = "", AID = "",
                telephone = ""
        )
    }

}
