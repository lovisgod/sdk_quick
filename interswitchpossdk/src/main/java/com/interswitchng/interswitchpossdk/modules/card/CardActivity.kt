package com.interswitchng.interswitchpossdk.modules.card

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.modules.card.model.CardTransactionState
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.shared.interfaces.library.EmvCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IsoService
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.printslips.info.TransactionType
import com.interswitchng.interswitchpossdk.shared.models.transaction.EmvResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.PaymentType
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.interswitchpossdk.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import com.interswitchng.interswitchpossdk.shared.utilities.DisplayUtils
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import kotlinx.android.synthetic.main.isw_activity_card.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*

class CardActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private val logger by lazy { Logger.with("CardActivity") }

    private val emvCallback by lazy { CardCallback() }
    private val emv by lazy { posDevice.getEmvCardTransaction() }

    // get strings of first item
    private val firstItem = "Choose Account"
    private var selectedItem = ""

    private val isoService: IsoService by inject()
    private val store: IKeyValueStore by inject()
    private lateinit var accountType: AccountType
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_card)

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount)
        amountText.text = getString(R.string.isw_amount, amount)
    }

    override fun onStart() {
        super.onStart()
        setupUI()
        setupTransaction()
    }

    override fun onDestroy() {
        super.onDestroy()
        emv.removeEmvCallback(emvCallback)
        emv.cancelTransaction()
    }

    private fun setupUI() {
        accountOptions.onItemSelectedListener = this
        accountOptions.adapter = ArrayAdapter.createFromResource(this, R.array.isw_account_types, R.layout.isw_spinner_item_label)
        continueButton.setOnClickListener {
            if (selectedItem == "") {
                toast("Choose a valid account type")
            } else {
                continueButton.isEnabled = false
                continueButton.isClickable = false
                startTransaction()
            }
        }
    }

    private fun setupTransaction() {
        Thread {

            // attach callback for emv transaction
            emv.setEmvCallback(emvCallback)

            // setup mock terminal info
            val empty = ""
            val terminalInfo = TerminalInfo("20390007", "20390007",
                    empty, empty, "0566", "0566",
                    1200, 1200)

            // setup card transaction
            // todo change amount to kobo
            emv.setupTransaction(paymentInfo.amount, terminalInfo)

            runOnUiThread {
                dialog.dismiss()
                // enable user select account options
                // only after detecting card
                paymentHint.text = getString(R.string.isw_hint_account_type)
                accountOptions.isClickable = true
                // show continue button container
                showContainer(CardTransactionState.ChooseAccountType)
            }

        }.start()
    }

    private fun startTransaction() {
        Thread {
            // start card transaction
            val result = emv.startTransaction()

            when (result) {
                EmvResult.OFFLINE_APPROVED -> emv.completeTransaction()
                EmvResult.ONLINE_REQUIRED -> logger.log("online should be processed").also { processOnline() }
                EmvResult.OFFLINE_DENIED -> {
                    toast("Transaction Declined")
                    showContainer(CardTransactionState.Default)
                }
            }

        }.start()
    }

    private fun showLoader(title: String = "Processing", message: String) {
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.show()
    }

    private fun showContainer(state: CardTransactionState) {
        val container = when(state) {
            CardTransactionState.ShowInsertCard -> insertCardContainer
            CardTransactionState.ChooseAccountType -> continueButtonContainer
            CardTransactionState.EnterPin -> insertPinContainer
            CardTransactionState.Default -> blankContainer
        }

        runOnUiThread { container.bringToFront() }
    }

    private fun cancelTransaction(reason: String) {
        runOnUiThread {
            setResult(Activity.RESULT_CANCELED)
            toast(reason)
            finish()
        }
    }

    private fun processOnline() {
        runOnUiThread {
            // change hint text
            paymentHint.text = getString(R.string.isw_title_processing_transaction)
            // hide other layouts: show default screen
            showContainer(CardTransactionState.Default)
            // show transaction progress alert
            showProgressAlert()
        }

        // TODO refactor this function [extremely ugly!!]
        TerminalInfo.get(store)?.let { terminalInfo ->
            val emv = emv.getTransactionInfo()

            if (emv != null) {
                val txnInfo = TransactionInfo.fromEmv(emv, paymentInfo.amount, PurchaseType.Card, accountType)
                val response = isoService.initiatePurchase(terminalInfo, txnInfo)
                // used default transaction because the
                // transaction is not processed by isw directly
                val txn = Transaction.default()

                val now = Date()
                response?.let {

                    val responseMsg = IsoUtils.getIsoResultMsg(it.code) ?: "Unknown Error"
                    val pinStatus = when{
                        pinOk || it.code == "00" -> "PIN Verified"
                        else -> "PIN Unverified"
                    }

                    transactionResult = TransactionResult(
                            paymentType = PaymentType.Card,
                            dateTime = DisplayUtils.getIsoString(now),
                            amount = DisplayUtils.getAmountString(paymentInfo.amount),
                            type = TransactionType.Purchase,
                            authorizationCode = response.code,
                            responseMessage = responseMsg,
                            responseCode = response.code,
                            cardPan = txnInfo.cardPAN, cardExpiry = txnInfo.cardExpiry, cardType = "",
                            stan = response.stan, pinStatus = pinStatus, AID = emv.AID, code = "",
                            telephone = "08031150978")

                    // TODO complete transaction using response from server

                    // show transaction result screen
                    showTransactionResult(txn)
                } ?: toast("Unable to process Transaction").also { finish() }

            } else {
                toast("Unable to get icc").also { finish() }
            }
        } ?: toast("No terminal info, found on device").also { finish() }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        if (firstItem == accountOptions.selectedItem) {
            selectedItem = ""
            // ensure card has been detected first
            if (accountOptions.isClickable)
                paymentHint.text = getString(R.string.isw_hint_account_type)
        } else {
            selectedItem = parent.getItemAtPosition(pos).toString()
            accountType = when {
                selectedItem.contains("Savings") -> AccountType.Savings
                selectedItem.contains("Current") -> AccountType.Current
                selectedItem.contains("Credit") -> AccountType.Credit
                else -> AccountType.Default
            }

            Toast.makeText(parent.context, "You have selected : $selectedItem", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNothingSelected(arg: AdapterView<*>) {}



    override fun getTransactionResult(transaction: Transaction): TransactionResult? = transactionResult

    internal inner class CardCallback : EmvCallback {

        override fun showInsertCard() {
            runOnUiThread {
                showContainer(CardTransactionState.ShowInsertCard)
                paymentHint.text = getString(R.string.isw_hint_insert_card)
            }
        }

        override fun onCardDetected() {
            runOnUiThread {
                showContainer(CardTransactionState.Default)
                showLoader("Reading Card", "Loading...")
            }
        }

        override fun showEnterPin() {
            showContainer(CardTransactionState.EnterPin)
        }

        override fun setPinText(text: String) {
            runOnUiThread { cardPin.setText(text) }
        }

        override fun showPinOk() {
            pinOk = true
            runOnUiThread { toast("Pin Input ok") }
        }

        override fun onCardRemoved() {
            cancelTransaction("Transaction Cancelled: Card was removed")
        }

        override fun onTransactionCancelled(code: Int, reason: String) {
            cancelTransaction(reason)
        }
    }
}
