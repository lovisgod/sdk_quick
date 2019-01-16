package com.interswitchng.interswitchpossdk.modules.ussdqr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.interswitchng.interswitchpossdk.IswPos
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.interfaces.Payable
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest
import com.interswitchng.interswitchpossdk.shared.models.request.CodeRequest.Companion.TRANSACTION_USSD
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.activity_ussd.*
import org.koin.android.ext.android.inject
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.shared.models.request.TransactionStatus
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction


class UssdActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val paymentService: Payable by inject()

    private var ussdCode: String? = null

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }


    //get strings of first item
    private val firstItem = "Choose a bank"
    private var selectedItem = ""

    private lateinit var bankCodes: MutableMap<String, String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ussd)
        setSupportActionBar(toolbar)

    }

    override fun onStart() {
        super.onStart()
        setupUI()
    }

    private fun setupUI() {
        loadBanks()
        banks.onItemSelectedListener = this
        btnGetCode.setOnTouchListener { _, _ -> getBankCode();true }

    }

    private fun loadBanks() {
        paymentService.getBanks { allBanks, throwable ->
            if (throwable != null) {
                // TODO handle error
            } else {
                bankCodes = mutableMapOf(firstItem to "")
                allBanks?.map { bankCodes.put(it.name, it.code) }
                val bankNames = bankCodes.keys.toList()
                banks.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, bankNames)
            }
        }
    }

    private fun getBankCode() {
        if (selectedItem == "" || selectedItem == firstItem) {
            Toast.makeText(this, "You have to select a Bank", Toast.LENGTH_LONG).show()
        } else {

            val instance = IswPos.getInstance()
            // get the payment info
            val paymentInfo: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)
            // set the selected bank-code for payment
            val bankCode =
                    if(bankCodes.containsKey(selectedItem)) bankCodes[selectedItem]!!
                    else ""

            // create payment info with bank code
            val paymentInfoPrime = PaymentInfo(paymentInfo.amount, paymentInfo.stan, bankCode)
            val request = CodeRequest.from(instance.config, paymentInfoPrime, TRANSACTION_USSD)

            dialog.show()

            if (ussdCode != null) {
                ussdText.text = ussdCode
                dialog.dismiss()
            } else {
                // initiate ussd payment
                paymentService.initiateUssdPayment(request) { response, throwable ->
                    if (throwable != null) {
                        // TODO handle error
                    } else {
                        response?.apply {
                            ussdCode = response.bankShortCode ?: response.defaultShortCode
                            ussdText.text = ussdCode
                            dialog.dismiss()
                            checkTransactionStatus(TransactionStatus(request.transactionType,
                                    response.transactionReference!!, instance.config.merchantCode))
                        }
                    }
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (firstItem == banks.selectedItem) {
            selectedItem = ""
        } else {
            selectedItem = parent.getItemAtPosition(pos).toString()
            Toast.makeText(parent.context, "You have selected : $selectedItem", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNothingSelected(arg: AdapterView<*>) {

    }

    override fun onTransactionSuccessful(transaction: Transaction) {
        Toast.makeText(this, "amount of ${transaction.amount} paid successfully", Toast.LENGTH_LONG).show()
    }

}
