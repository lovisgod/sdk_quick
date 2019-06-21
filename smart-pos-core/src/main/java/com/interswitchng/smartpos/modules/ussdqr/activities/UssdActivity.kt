package com.interswitchng.smartpos.modules.ussdqr.activities

import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.widget.Toast
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


    private val printSlip = mutableListOf<PrintObject>()
    // bottom sheet dialog for banks
    private lateinit var banksDialog: SelectBankBottomSheet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_ussd)
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

        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)

        // load bank list immediately
        ussdViewModel.loadBanks()

        paymentHint.text = getString(R.string.isw_select_bank)
        banks.text = getString(R.string.isw_select_bank)
        banks.setOnClickListener {
            // create dialog if not existing
            if (!::banksDialog.isInitialized)
                banksDialog = SelectBankBottomSheet.newInstance()

            //  show dialog
            banksDialog.show(supportFragmentManager, banksDialog.tag)
        }
        banks.performClick()

        // observe viewModel
        observeViewModel()
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
                it?.apply(::handlePaymentStatus)
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

            // observe printer
        }

        // observe bank selection
        banksDialog.selectedBank.observe(owner) {
            it?.apply(::getBankCode)
        }

    }

    private fun getBankCode(selectedBank: Bank) {
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
    }

    private fun showButtons(response: CodeResponse) {
        initiateButton.isEnabled = true
        initiateButton.setOnClickListener {
            initiateButton.isEnabled = false
            initiateButton.isClickable = false

            // check transaction status
            ussdViewModel.checkTransactionStatus(TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode))
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            ussdViewModel.printCode(this, posDevice, UserType.Customer, printSlip)
        }
    }


    private fun handleResponse(response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {

                ussdCode = response.bankShortCode ?: response.defaultShortCode
                ussdCode?.apply {
                    ussdText.text = this
                    val code = substring(lastIndexOf("*") + 1 until lastIndexOf("#"))
                    paymentHint.text = HtmlCompat.fromHtml(getString(R.string.isw_hint_enter_ussd_code, code), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    printSlip.add(PrintObject.Data("code \n $this\n", PrintStringConfiguration(isBold = true, isTitle = true)))
                }

                // show buttons
                showButtons(response)

                // check transaction status
                val status = TransactionStatus(response.transactionReference!!, iswPos.config.merchantCode)
                ussdViewModel.pollTransactionStatus(status)
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
                telephone = iswPos.config.merchantTelephone
        )
    }

    override fun onCheckStopped() {
        super.onCheckStopped()
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
    }
}
