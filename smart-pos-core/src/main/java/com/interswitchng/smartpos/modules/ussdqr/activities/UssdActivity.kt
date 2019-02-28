package com.interswitchng.smartpos.modules.ussdqr.activities

import android.os.Bundle
import android.widget.Toast
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.ussdqr.views.SelectBankBottomSheet
import com.interswitchng.smartpos.shared.interfaces.library.Payable
import com.interswitchng.smartpos.shared.interfaces.PaymentInitiator
import com.interswitchng.smartpos.shared.interfaces.PaymentRequest
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.core.UserType
import com.interswitchng.smartpos.shared.models.posconfig.PrintObject
import com.interswitchng.smartpos.shared.models.posconfig.PrintStringConfiguration
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.CodeRequest.Companion.TRANSACTION_USSD
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Bank
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import kotlinx.android.synthetic.main.isw_activity_ussd.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*


class UssdActivity : BaseActivity(), SelectBankBottomSheet.SelectBankCallback {

    private val paymentService: Payable by inject()
    private val initiator: PaymentInitiator by inject()

    private var ussdCode: String? = null
    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val alert by lazy { DialogUtils.getAlertDialog(this) }
    private val logger by lazy { Logger.with("USSD") }

    // getResult strings of first item
    private val firstItem = "Choose a bank"
    private var selectedItem = ""

    private var selectedBank: Bank? = null
    // container for banks and bank-codes
    private var allBanks: List<Bank>? = null
    private val printSlip = mutableListOf<PrintObject>()


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

        val amount = DisplayUtils.getAmountString(paymentInfo.amount / 100)
        amountText.text = getString(R.string.isw_amount, amount)

        // loadBanks()

        paymentHint.text = "Loading Banks..."
        banks.text = getString(R.string.isw_select_bank)
        banks.setOnClickListener {
            val dialog = SelectBankBottomSheet.newInstance()
            dialog.show(supportFragmentManager, dialog.tag)
        }
        banks.performClick()
    }

    override fun loadBanks(callback: (List<Bank>) -> Unit) {

        if (allBanks != null) callback(allBanks!!)

        else paymentService.getBanks { allBanks, throwable ->
            if (throwable != null) {
                // TODO handle error
            } else {
                allBanks?.let {
                    this.allBanks = it
                    runOnUiThread { callback(it) }
                }
            }
        }
    }

    private fun getBankCode() {

        if (selectedBank == null) {
            Toast.makeText(this, "You have to select a Bank", Toast.LENGTH_LONG).show()
            return
        }

        val bankCode = selectedBank?.code
        // create payment info with bank code
        val paymentInfoPrime = PaymentInfo(paymentInfo.amount, paymentInfo.stan, bankCode)
        val request = CodeRequest.from(terminalInfo, paymentInfoPrime, TRANSACTION_USSD)

        dialog.show()

        // initiate ussd payment
        paymentService.initiateUssdPayment(request) { response, throwable ->
            runOnUiThread {
                // handle error or response
                if (throwable != null) handleError(throwable)
                else response?.apply { handleResponse(request, this) }
            }
        }
    }

    private fun showMockButtons(request: CodeRequest, response: CodeResponse) {
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
        printCodeButton.setOnClickListener { printCode() }
    }


    private fun printCode() {
        // get printer status
        val printStatus = posDevice.printer.canPrint()

        // print based on status
        when(printStatus) {
            is Error -> toast(printStatus.message)
            else -> {
                printCodeButton.isEnabled = false
                printCodeButton.isClickable = false

                val disposable = ThreadUtils.createExecutor {
                    val status = posDevice.printer.printSlip(printSlip, UserType.Customer)

                    runOnUiThread {
                        Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                        printCodeButton.isEnabled = true
                        printCodeButton.isClickable = false
                    }
                }

                disposables.add(disposable)
            }
        }
    }

    private fun handleResponse(request: CodeRequest, response: CodeResponse) {
        when (response.responseCode) {
            CodeResponse.OK -> {

                ussdCode = response.bankShortCode ?: response.defaultShortCode
                ussdCode?.apply {
                    ussdText.text = this
                    printSlip.add(PrintObject.Data("code \n $this\n", PrintStringConfiguration(isBold = true, isTitle = true)))
                }
                dialog.dismiss()

                // TODO remove mock trigger
                showMockButtons(request, response)

                // check transaction status
                handler.postDelayed({checkTransactionStatus(TransactionStatus(response.transactionReference!!, instance.config.merchantCode)) }, 60_000)
            }
            else -> {
                runOnUiThread {
                    val errorMessage = "An error occured: ${response.responseDescription}"
                    toast(errorMessage)
                    showAlert()
                }
            }
        }
    }

    private fun handleError(throwable: Throwable) {
        toast(throwable.localizedMessage)
        showAlert()
    }

    private fun showAlert() {
        alert.setPositiveButton(R.string.isw_title_try_again) { dialog, _ -> dialog.dismiss(); getBankCode() }
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
                amount = DisplayUtils.getAmountString(paymentInfo.amount / 100),
                type = TransactionType.Purchase,
                authorizationCode = transaction.responseCode,
                responseMessage = responseMsg,
                responseCode = transaction.responseCode,
                cardPan = "", cardExpiry = "", cardType = "",
                stan = paymentInfo.stan, pinStatus = "", AID = "", code = ussdCode!!,
                telephone = "08031140978"
        )
    }

    override fun onCheckError() {
        initiateButton.isEnabled = true
        initiateButton.isClickable = true
    }

    override fun onBankSelected(bank: Bank) {
        banks.text = bank.name
        selectedItem = bank.name

        selectedBank = bank

        getBankCode()
    }
}
