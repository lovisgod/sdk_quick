package com.interswitchng.interswitchpossdk.modules.ussdqr

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentInitiator
import com.interswitchng.interswitchpossdk.shared.interfaces.PaymentRequest
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.posconfig.PrintObject
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest.Companion.TRANSACTION_USSD
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.CodeResponse
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import kotlinx.android.synthetic.main.activity_ussd.*
import kotlinx.android.synthetic.main.content_toolbar.*
import org.koin.android.ext.android.inject
import java.text.NumberFormat


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
    private val prints = mutableListOf<PrintObject>()
    // get the payment info
    private lateinit var paymentInfo: PaymentInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ussd)

        setSupportActionBar(toolbar)
        toolbar.title = "USSD"
        paymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
    }

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {

        val amount = NumberFormat.getInstance().format(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)

        loadBanks()
        banks.onItemSelectedListener = this
        btnGetCode.setOnClickListener { _ ->
            showGetCodeButton(false)
            getBankCode()
        }

    }

    private fun loadBanks() {
        paymentService.getBanks { allBanks, throwable ->
            if (throwable != null) {
                // TODO handle error
            } else {
                bankCodes = mutableMapOf(firstItem to "")
                allBanks?.map { bankCodes.put(it.name, it.code) }
                val bankNames = bankCodes.keys.toList()
                runOnUiThread { banks.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, bankNames) }
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
            checkTransactionStatus(TransactionStatus(response.transactionReference!!, instance.config.merchantCode))
        }

        printCodeButton.isEnabled = true
        printCodeButton.setOnClickListener {
            printCodeButton.isEnabled = false
            posDevice.printReceipt(prints)
            Toast.makeText(this, "Printing Code", Toast.LENGTH_LONG).show()
            printCodeButton.isEnabled = true
        }
    }

    private fun handleResponse(request: CodeRequest, response: CodeResponse) {
        ussdCode = response.bankShortCode ?: response.defaultShortCode
        ussdCode?.apply {
            ussdText.text = this
            prints.add(PrintObject.Data( this))
        }
        dialog.dismiss()

        // TODO remove mock trigger
        showMockButtons(request, response)
    }

    private fun handleError(throwable: Throwable) {
        // TODO handle error
        Toast.makeText(this, throwable.localizedMessage, Toast.LENGTH_LONG).show()
    }


    private fun showGetCodeButton(shouldShow: Boolean) {
        val visibility = if (shouldShow) View.VISIBLE else View.GONE
        btnGetCode.visibility = visibility
        btnGetCode.isEnabled = shouldShow
    }

    private fun showMockButtons(shouldShow: Boolean) {
        val visibility = if (shouldShow) View.VISIBLE else View.GONE
        mockButtonsContainer.visibility = visibility
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (firstItem == banks.selectedItem) {
            selectedItem = ""
            showGetCodeButton(false)
        } else {
            selectedItem = parent.getItemAtPosition(pos).toString()
            Toast.makeText(parent.context, "You have selected : $selectedItem", Toast.LENGTH_LONG).show()
            showGetCodeButton(true)
        }

        // stop any polling
        stopPolling()
        showMockButtons(false)
    }

    override fun onNothingSelected(arg: AdapterView<*>) {

    }

    override fun onTransactionSuccessful(transaction: Transaction) {
        completeButtonsContainer.visibility = View.VISIBLE

        printBtn.setOnClickListener {
            printBtn.isClickable = false
            printBtn.isEnabled = false

            Toast.makeText(this, "Printing Receipt", Toast.LENGTH_LONG).show()
            posDevice.printReceipt(prints)
            printBtn.isClickable = true
            printBtn.isEnabled = true
        }

        doneButton.setOnClickListener { finish() }
        Toast.makeText(this, "amount of ${transaction.amount} paid successfully", Toast.LENGTH_LONG).show()
    }
}
