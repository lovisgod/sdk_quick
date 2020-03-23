package com.interswitchng.smartpos.modules.ussdqr.activities

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import com.gojuno.koptional.None
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.ussdqr.viewModels.UssdViewModel
import com.interswitchng.smartpos.modules.ussdqr.views.SelectBankBottomSheet
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest.Companion.TRANSACTION_USSD
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.PaymentStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.*
import kotlinx.android.synthetic.main.isw_activity_ussd.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class UssdActivity : BaseActivity() {

    private val ussdViewModel: UssdViewModel by viewModel()

    private var ussdCode: String? = null
    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val alert by lazy { DialogUtils.getAlertDialog(this) }
    private val logger by lazy { Logger.with("USSD") }


    private var printSlip = mutableListOf<PrintObject>()
    // bottom sheet dialog for banks
    private lateinit var banksDialog: SelectBankBottomSheet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_ussd)
        setupUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }

    private fun setupUI() {

        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)

        if (DeviceUtils.isConnectedToInternet(this)) {

            paymentHint.text = getString(R.string.isw_select_bank)
            banks.text = getString(R.string.isw_select_bank)
            banks.setOnClickListener {
                // create dialog if not existing
                if (!::banksDialog.isInitialized)
                    banksDialog = SelectBankBottomSheet.newInstance()

                // load banks if not loaded
                if (!banksDialog.hasBanks) runWithInternet {
                    ussdViewModel.loadBanks()
                }

                //  show dialog
                banksDialog.show(supportFragmentManager, banksDialog.tag)
            }
            banks.performClick()

            // observe viewModel
            observeViewModel()
        } else runWithInternet {
            setupUI()
        }

    }

    private fun observeViewModel() {
        // func to return lifecycle
        val owner = { lifecycle }

        // observe view model
        with(ussdViewModel) {

            // observe print button
            printButton.observe(owner) {
                val isEnabled = it ?: false
                printCodeButton.isEnabled = isEnabled
                printCodeButton.isClickable = isEnabled
            }

            // observe bank list
            allBanks.observe(owner) {
                it?.let { banks ->
                    when (banks) {
                        is Some -> banksDialog.loadBanks(banks.value)
                        is None -> banksDialog.dismiss().also { toast("Unable to load bank list") }
                    }
                }
            }

            // observe selected bank code
            bankCode.observe(owner) {
                // dismiss loading dialog
                dialog.dismiss()

                // handle code response
                it?.let { code ->
                    when (code) {
                        is Some -> handleResponse(code.value)
                        is None -> handleError()
                    }
                }
            }

            // observe payment status
            paymentStatus.observe(owner) {
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

            // observe progress bar
            showProgress.observe(owner) {
                if (it != true) return@observe

                // show progress alert
                showProgressAlert {
                    ussdViewModel.cancelPoll()
                    onCheckStopped()
                }
            }
        }

        // observe bank selection
        banksDialog.selectedBank.observe(owner) {
            it?.apply(::getBankCode)
        }

    }

    private fun getBankCode(selectedBank: Bank) {

        if (DeviceUtils.isConnectedToInternet(this)) {

            // set bank info
            banks.text = selectedBank.name
            paymentHint.text = selectedBank.name


            // show loading dialog
            dialog.show()

            // create payment info with bank code
            val info = PaymentInfo(paymentInfo.amount, selectedBank.code)
            val request = CodeRequest.from(iswPos.config.alias, terminalInfo, info, TRANSACTION_USSD)

            // get ussd code
            ussdViewModel.getBankCode(request)
        } else runWithInternet {
            // re-trigger get bank code
            getBankCode(selectedBank)
        }
    }

    private fun showButtons(response: CodeResponse) {
        initiateButton.isEnabled = true
        initiateButton.setOnClickListener {
            if (DeviceUtils.isConnectedToInternet(this)) {
                initiateButton.isEnabled = false
                initiateButton.isClickable = false

                val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                // check transaction status
                ussdViewModel.checkTransactionStatus(status)

            } else runWithInternet {
                // re-perform click
                initiateButton.performClick()
            }
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            ussdViewModel.printCode(this, posDevice, UserType.Customer, printSlip)
        }
    }


    private fun handleResponse(response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {

                // ensure internet connection before polling for transaction
                if (DeviceUtils.isConnectedToInternet(this)) {

                    ussdCode = response.bankShortCode ?: response.defaultShortCode
                    ussdCode?.apply {
                        ussdText.text = this
                        val code = substring(lastIndexOf("*") + 1 until lastIndexOf("#"))

                        // get the entire hint as spannable string
                        val hint = getString(R.string.isw_hint_enter_ussd_code, code)
                        val spannableHint = SpannableString(hint)


                        // increase font size and change font color
                        val startIndex = 16
                        val endIndex = startIndex + code.length + 1
                        val codeColor = ContextCompat.getColor(this@UssdActivity, R.color.iswColorPrimaryDark)
                        spannableHint.setSpan(AbsoluteSizeSpan(24, true), startIndex, endIndex, 0)
                        spannableHint.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
                        spannableHint.setSpan(ForegroundColorSpan(codeColor), startIndex, endIndex, 0)


                        // set the text value
                        paymentHint.text = spannableHint
                        printSlip.add(PrintObject.Data("code - \n $this\n", PrintStringConfiguration(isBold = true, isTitle = true)))
                    }

                    // show buttons
                    showButtons(response)

                    // check transaction status
                    val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                    ussdViewModel.pollTransactionStatus(status)

                } else runWithInternet {
                    // re-trigger handle response
                    handleResponse(response)
                }
            }
            else -> {
                runOnUiThread {
                    val errorMessage = "An error occured: ${response.responseDescription}"
                    toast(errorMessage)
                    handleError()
                }
            }
        }
    }

    private fun handleError() {
        val selectedBank = banksDialog.selectedBank.value!!
        alert.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); getBankCode(selectedBank) }
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()

        val responseMsg = IsoUtils.getIsoResult(transaction.responseCode)?.second
                ?: transaction.responseDescription
                ?: "Error"

        return TransactionResult(
                paymentType = PaymentType.USSD,
                dateTime = DisplayUtils.getIsoString(now),
                amount = DisplayUtils.getAmountString(paymentInfo),
                type = TransactionType.Purchase,
                authorizationCode = transaction.responseCode,
                responseMessage = responseMsg,
                responseCode = transaction.responseCode,
                cardPan = "", cardExpiry = "", cardType = CardType.None,
                stan = paymentInfo.getStan(), pinStatus = "", AID = "", code = ussdCode!!,
                telephone = iswPos.config.merchantTelephone, src = "", icc = "", cardPin = "", cardTrack2 = "", csn = "", time = -1L
        )
    }

    override fun onCheckStopped() {
        super.onCheckStopped()
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
        ussdViewModel.cancelPoll()
    }
}
