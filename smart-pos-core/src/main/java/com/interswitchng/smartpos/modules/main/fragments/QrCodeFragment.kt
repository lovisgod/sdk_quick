package com.interswitchng.smartpos.modules.main.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.ussdqr.viewModels.QrViewModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DeviceUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_processing_transaction.*
import kotlinx.android.synthetic.main.isw_fragment_qr_code.*
import kotlinx.android.synthetic.main.isw_generating_code_layout.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class QrCodeFragment : BaseFragment(TAG) {

    private val qrViewModel: QrViewModel by viewModel()

    private val dialog by lazy { context?.let { DialogUtils.getLoadingDialog(it) } }
    private val alert by lazy { context?.let { DialogUtils.getAlertDialog(it) } }

    private var qrData: String? = null
    private var printSlip = mutableListOf<PrintObject>()

    private val qrCodeFragmentArgs by navArgs<QrCodeFragmentArgs>()
    private val paymentModel by lazy { qrCodeFragmentArgs.PaymentModel }

    private lateinit var paymentTypeDialog: PaymentTypeDialog

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan())
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_qr_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }

    private fun setupUI() {
        // func returns lifecycle
        val owner = { lifecycle }
        showPaymentOptions()
        isw_card_details_toolbar.setNavigationOnClickListener { navigateUp() }

        configureProgressBar()

        // observe view model
        with(qrViewModel) {

            // observe qr code
            qrCode.observe(owner) {
                it?.let { code ->
                    dialog?.dismiss()

                    when (code) {
                        is Some -> handleResponse(code.value)
                        is None -> handleError()
                    }
                }
            }

            // observe payment status
            paymentStatus.observe(owner) {
                // handle updates of payment status
                it?.let { status ->
                    // setup payment slip
                    if (it is PaymentStatus.Timeout) {
                        val transaction = Transaction(
                                -1, paymentInfo.amount,
                                "",
                                "0X0X",
                                terminalInfo.currencyCode,
                                true,
                                null,
                                0,
                                "Pending")

                        val result = getTransactionResult(transaction)
                        printSlip = result?.getSlip(terminalInfo)?.getSlipItems() ?: printSlip
                    }

                    handlePaymentStatus(status)
                }
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
        // ensure device is connected to internet before
        if (DeviceUtils.isConnectedToInternet(context!!)) {
            // show loading dialog
            dialog?.show()

            // create and request code
            val request = CodeRequest.from(iswPos.config.alias, terminalInfo,
                    paymentInfo, CodeRequest.TRANSACTION_QR, CodeRequest.QR_FORMAT_RAW
            )

            qrViewModel.getQrCode(request, context!!)
        } else runWithInternet {
            getQrCode()
        }
    }

    private fun showPaymentOptions() {
        change_payment_method.setOnClickListener {
            paymentTypeDialog = PaymentTypeDialog(PaymentModel.PaymentType.CARD) {
                when (it) {
                    PaymentModel.PaymentType.QR_CODE -> {
                    }
                    PaymentModel.PaymentType.PAY_CODE -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentPayCode(paymentModel)
                        navigate(direction)
                    }
                    PaymentModel.PaymentType.CARD -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentCardTransactions(paymentModel)
                        navigate(direction)
                    }
                    PaymentModel.PaymentType.USSD -> {
                        val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentUssd(paymentModel)
                        navigate(direction)
                    }
                }

            }
            paymentTypeDialog.show(childFragmentManager, CardTransactionsFragment.TAG)
        }
    }

    private fun handleResponse(response: CodeResponse) {
        updateProgressBarAndSwitchView()

        when (response.responseCode) {
            CodeResponse.OK -> {
                if (DeviceUtils.isConnectedToInternet(context!!)) {

                    qrData = response.qrCodeData
                    val bitmap = response.qrCodeImage!!
                    printSlip.add(PrintObject.BitMap(bitmap))

                    isw_qr_code_image.setImageBitmap(response.qrCodeImage)
                    //showTransactionMocks(response)

                    // check transaction status
                    val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)

                    // ensure internet connection before
                    // polling for transaction result
                    qrViewModel.pollTransactionStatus(status)
                } else runWithInternet {
                    handleResponse(response)
                }
            }
            else -> {
                val errorMessage = "An error occured: ${response.responseDescription}"
                context?.toast(errorMessage)
                handleError()
            }
        }
    }

    private fun handleError() {
        alert?.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); getQrCode() }
                ?.setNegativeButton(R.string.isw_title_cancel) { dialog, _ -> dialog.dismiss() }
                ?.show()
    }


    private fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()
        val responseMsg = IsoUtils.getIsoResultMsg(transaction.responseCode)
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
                code = qrData!!, telephone = iswPos.config.merchantTelephone, csn = "", icc = "", cardTrack2 = "", cardPin = "",
                src = "",
                time = -1L
        )
    }


    override fun onCheckStopped() {
        super.onCheckStopped()
        //initiateButton.isEnabled = true
        //initiateButton.isClickable = true
        qrViewModel.cancelPoll()
    }

    private fun configureProgressBar() {
        isw_processing_text?.text = "Generating QR code"
        isw_progressbar.progressTintList = ColorStateList.valueOf(Color.WHITE)
    }

    private fun updateProgressBarAndSwitchView() {
        isw_progressbar.progress = 90
        isw_qr_code_view_animator.displayedChild = 1
        isw_qr_code_amount.text = paymentModel.formattedAmount
    }

    companion object {
        const val TAG = "QR Code"
    }
}