package com.interswitchng.interswitchpossdk.modules.ussdqr


import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.interfaces.library.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentInitiator
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentRequest
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
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
import kotlinx.android.synthetic.main.isw_activity_qr_code.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*


class QrCodeActivity : BaseActivity() {

    private val paymentService: Payable by inject()
    private val store: IKeyValueStore by inject()

    // TODO remove reference
    private val initiator: PaymentInitiator by inject()

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val logger by lazy { Logger.with("QR") }
    private val alert by lazy { DialogUtils.getAlertDialog(this) }

    private var qrData: String? = null
    private var qrBitmap: Bitmap? = null
    private val printSlip = mutableListOf<PrintObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_qr_code)
    }

    override fun onStart() {
        super.onStart()
        setupImage()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }


    private fun setupImage() {

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount / 100)
        amountText.text = getString(R.string.isw_amount, amount)
        paymentHint.text = getString(R.string.isw_hint_qr_code)

        dialog.show()

        if (qrBitmap != null) {
            qrCodeImage.setImageBitmap(qrBitmap)
            dialog.dismiss()
        } else {
            val request = CodeRequest.from(terminalInfo, paymentInfo, TRANSACTION_QR, QR_FORMAT_RAW)
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
            checkTransactionStatus(TransactionStatus(response.transactionReference, instance.merchantCode))
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
                qrData = response.qrCodeData
                qrBitmap = response.getBitmap(this)
                val bitmap = PrintObject.BitMap(qrBitmap!!)
                printSlip.add(bitmap)
                runOnUiThread {
                    qrCodeImage.setImageBitmap(qrBitmap)
                    // TODO remove mock trigger
                    showTransactionMocks(request, response)
                }
            }
            else -> {
                runOnUiThread {
                    val errorMessage = "An error occured: ${response.responseDescription}"
                    toast(errorMessage)
                    showAlert()
                }
            }
        }

        runOnUiThread { dialog.dismiss() }
    }

    private fun handleError(throwable: Throwable) {
        // TODO handle error
        toast(throwable.localizedMessage)
        dialog.dismiss()
        showAlert()
    }

    private fun showAlert() {
        alert.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); setupImage() }
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ -> dialog.dismiss() }
                .show()
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
                code = qrData!!, telephone = "08031140978"
        )
    }


    override fun onCheckError() {
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
    }

}
