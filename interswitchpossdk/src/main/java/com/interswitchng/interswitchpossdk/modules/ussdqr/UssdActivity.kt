package com.interswitchng.interswitchpossdk.modules.ussdqr

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.R
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
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.CodeRequest.Companion.TRANSACTION_USSD
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import com.interswitchng.interswitchpossdk.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.activity_ussd.*
import kotlinx.android.synthetic.main.content_amount.*
import org.koin.android.ext.android.inject
import java.util.*


class UssdActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val paymentService: Payable by inject()
    private val initiator: PaymentInitiator by inject()

    private var ussdCode: String? = null
    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val logger by lazy { Logger.with("USSD") }

    // get strings of first item
    private val firstItem = "Choose a bank"
    private var selectedItem = ""
    // container for banks and bank-codes
    private lateinit var bankCodes: MutableMap<String, String>
    private val printSlip = mutableListOf<PrintObject>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ussd)
    }

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {

        val amount = DisplayUtils.getAmountString(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)

        loadBanks()

        paymentHint.text = "Loading Banks"
        banks.onItemSelectedListener = this
        changePaymentMethod.setOnClickListener {
            showPaymentOptions(BottomSheetOptionsDialog.USSD)
        }

        showMockButtons(false)

    }

    private fun loadBanks() {
        paymentService.getBanks { allBanks, throwable ->
            if (throwable != null) {
                // TODO handle error
            } else {
                bankCodes = mutableMapOf(firstItem to "")
                allBanks?.map { bankCodes.put(it.name, it.code) }
                val bankNames = bankCodes.keys.toList()
                runOnUiThread { banks.adapter = ArrayAdapter(this, R.layout.list_item_spinner_option, bankNames) }
            }
        }
    }

    private fun getBankCode() {
        if (selectedItem == "" || selectedItem == firstItem) {
            Toast.makeText(this, "You have to select a Bank", Toast.LENGTH_LONG).show()
        } else {
            // set the selected bank-code for payment
            val bankCode =
                    if (bankCodes.containsKey(selectedItem)) bankCodes[selectedItem]!!
                    else ""

            // create payment info with bank code
            val paymentInfoPrime = PaymentInfo(paymentInfo.amount, paymentInfo.stan, bankCode)
            val request = CodeRequest.from(instance.config, paymentInfoPrime, TRANSACTION_USSD)

            dialog.show()

            // initiate ussd payment
            paymentService.initiateUssdPayment(request) { response, throwable ->
                // handle error or response
                if (throwable != null) handleError(throwable)
                else response?.apply { runOnUiThread { handleResponse(request, this) } }
            }

        }
    }

    private fun showMockButtons(request: CodeRequest, response: CodeResponse) {
        // TODO remove mock trigger
        initiateButton.isEnabled = true
        mockButtonsContainer.visibility = View.VISIBLE
        initiateButton.setOnClickListener {
            initiateButton.isEnabled = false
            mockButtonsContainer.visibility = View.GONE

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
            posDevice.printer.printSlip(printSlip, UserType.Customer)
            Toast.makeText(this, "Printing Code", Toast.LENGTH_LONG).show()
            printCodeButton.isEnabled = true
        }
    }

    private fun handleResponse(request: CodeRequest, response: CodeResponse) {
        ussdCode = response.bankShortCode ?: response.defaultShortCode
        ussdCode?.apply {
            ussdText.text = this
            printSlip.add(PrintObject.Data( this))
        }
        dialog.dismiss()

        // TODO remove mock trigger
        showMockButtons(request, response)
    }

    private fun handleError(throwable: Throwable) {
        // TODO handle error
        Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_LONG).show()
    }

    private fun showMockButtons(shouldShow: Boolean) {
        val visibility = if (shouldShow) View.VISIBLE else View.GONE
        mockButtonsContainer.visibility = visibility
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (firstItem == banks.selectedItem) {
            selectedItem = ""
            paymentHint.text = "Choose a bank to get a USSD code"
        } else {
            selectedItem = parent.getItemAtPosition(pos).toString()
            Toast.makeText(parent.context, "You have selected : $selectedItem", Toast.LENGTH_LONG).show()
            getBankCode()

            paymentHint.text = getString(R.string.pay_ussd_instruction)
        }

        // stop any polling
        stopPolling()
        showMockButtons(false)
    }

    override fun onNothingSelected(arg: AdapterView<*>) {}


    override fun getTransactionResult(transaction: Transaction): TransactionResult? {
        val now = Date()

        val responseMsg = IsoUtils.getIsoResult(transaction.responseCode)?.second
                ?: transaction.responseDescription
                ?: "Error"

        return TransactionResult(
                paymentType = PaymentType.USSD,
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
