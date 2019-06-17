package com.interswitchng.smartpos.modules.ussdqr.activities


import android.os.Bundle
import android.view.View
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest.Companion.QR_FORMAT_RAW
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest.Companion.TRANSACTION_QR
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import kotlinx.android.synthetic.main.isw_activity_qr_code.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class QrCodeActivity : BaseActivity() {

    private val qrViewModel: QrViewModel by viewModel()


    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val logger by lazy { Logger.with("QR") }
    private val alert by lazy { DialogUtils.getAlertDialog(this) }

    private var qrData: String? = null
    private val printSlip = mutableListOf<PrintObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_qr_code)
    }

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }


    private fun setupUI() {
        // func returns lifecycle
        val owner = { lifecycle }

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)
        paymentHint.text = getString(R.string.isw_hint_qr_code)

        // observe view model
        with(qrViewModel) {

            // observe print button
            printButton.observe(owner) {
                val isEnabled = it ?: false
                printCodeButton.isEnabled = isEnabled
                printCodeButton.isClickable = isEnabled
            }

            // observe qr code
            qrCode.observe(owner) {
                it?.let { code ->
                    dialog.dismiss()
                    when (code) {
                        is Some -> handleResponse(code.value)
                        is None -> handleError()
                    }
                }
            }

            // observe payment status
            paymentStatus.observe(owner) {
                // handle updates of payment status
                it?.let(::handlePaymentStatus)
            }

            showProgress.observe(owner) {
                if (it != true) return@observe

                // show progress alert
                showProgressAlert {
                    qrViewModel.cancelPoll()
                    onCheckStopped()
                }
            }
        }


        // get qr code
        getQrCode()
    }

    private fun getQrCode() {
        // show loading dialog
        dialog.show()

        // create and request code
        val request = CodeRequest.from(iswPos.config.alias, terminalInfo,
                paymentInfo, TRANSACTION_QR, QR_FORMAT_RAW)
        qrViewModel.getQrCode(request, this)
    }

    private fun handleResponse(response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {
                qrData = response.qrCodeData
                val bitmap = response.qrCodeImage!!
                printSlip.add(PrintObject.BitMap(bitmap))

                qrCodeImage.setImageBitmap(response.qrCodeImage)
                showTransactionMocks(response)

                // check transaction status
                val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                qrViewModel.pollTransactionStatus(status)
            }
            else -> {
                val errorMessage = "An error occured: ${response.responseDescription}"
                toast(errorMessage)
                handleError()
            }
        }
    }

    private fun handleError() {
        alert.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); getQrCode() }
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ -> dialog.dismiss() }
                .show()
    }



    private fun showTransactionMocks(response: CodeResponse) {
        mockButtonsContainer.visibility = View.VISIBLE
        initiateButton.isEnabled = true

        initiateButton.setOnClickListener {
            initiateButton.isEnabled = false
            initiateButton.isClickable = false

            // check transaction status
            val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
            qrViewModel.checkTransactionStatus(status)
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            qrViewModel.printCode(this, posDevice, UserType.Customer, printSlip)
        }

    }


    override fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()
        val responseMsg = IsoUtils.getIsoResult(transaction.responseCode)?.second
                ?: transaction.responseDescription
                ?: "Error"

        return TransactionResult(
                paymentType = PaymentType.QR,
                dateTime = DisplayUtils.getIsoString(now),
                amount = DisplayUtils.getAmountString(paymentInfo),
                type = TransactionType.Purchase,
                authorizationCode = transaction.responseCode,
                responseMessage = responseMsg,
                responseCode = transaction.responseCode,
                cardPan = "", cardExpiry = "", cardType = CardType.None,
                stan = paymentInfo.getStan(), pinStatus = "", AID = "",
                code = qrData!!, telephone = iswPos.config.merchantTelephone
        )
    }


    override fun onCheckStopped() {
        super.onCheckStopped()
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
    }

}
