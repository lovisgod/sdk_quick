package com.interswitchng.smartpos.modules.card

import android.app.Activity
import android.os.Bundle
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.model.CardTransactionState
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.interfaces.library.IKeyValueStore
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import kotlinx.android.synthetic.main.isw_activity_card.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*

class CardActivity : BaseActivity() {

    private val logger by lazy { Logger.with("CardActivity") }

    private val emvCallback by lazy { CardCallback() }
    private val emv by lazy { posDevice.getEmvCardTransaction() }

    private val isoService: IsoService by inject()
    private val store: IKeyValueStore by inject()
    private val accountType = AccountType.Default
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_card)

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo.amount / 100)
        amountText.text = getString(R.string.isw_amount, amount)
    }

    override fun onStart() {
        super.onStart()
        setupTransaction()
    }

    override fun onDestroy() {
        super.onDestroy()
        emv.removeEmvCallback(emvCallback)
        emv.cancelTransaction()
    }


    private fun setupTransaction() {
       val disposable =  ThreadUtils.createExecutor {

            // attach callback for emv transaction
            emv.setEmvCallback(emvCallback)

            // setup mock terminal info
            val empty = ""
            val terminalInfo = TerminalInfo("20390007", "20390007",
                    empty, empty, "0566", "0566",
                    1200, 1200)

            // setup card transaction
            emv.setupTransaction(paymentInfo.amount, terminalInfo)

            runOnUiThread { dialog.dismiss() }
            startTransaction()

        }

        disposables.add(disposable)
    }

    private fun startTransaction() {
       val disposable = ThreadUtils.createExecutor {
            // start card transaction
            val result = emv.startTransaction()

            when (result) {
                EmvResult.ONLINE_REQUIRED -> logger.log("online should be processed").also { processOnline() }
                else -> {
                    toast("Transaction Declined")
                    showContainer(CardTransactionState.Default)
                }
            }
        }

        disposables.add(disposable)
    }

    private fun showLoader(title: String = "Processing", message: String) {
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.show()
    }

    private fun showContainer(state: CardTransactionState) {
        val container = when(state) {
            CardTransactionState.ShowInsertCard -> insertCardContainer
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
            val emvData = emv.getTransactionInfo()

            if (emvData != null) {
                val txnInfo = TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)
                val response = isoService.initiateCardPurchase(terminalInfo, txnInfo)
                // used default transaction because the
                // transaction is not processed by isw directly
                val txn = Transaction.default()

                val now = Date()
                response?.let {

                    val responseMsg = IsoUtils.getIsoResultMsg(it.responseCode) ?: "Unknown Error"
                    val pinStatus = when{
                        pinOk || it.responseCode == IsoUtils.OK -> "PIN Verified"
                        else -> "PIN Unverified"
                    }

                    transactionResult = TransactionResult(
                            paymentType = PaymentType.Card,
                            dateTime = DisplayUtils.getIsoString(now),
                            amount = DisplayUtils.getAmountString(paymentInfo.amount / 100),
                            type = TransactionType.Purchase,
                            authorizationCode = response.authCode,
                            responseMessage = responseMsg,
                            responseCode = response.responseCode,
                            cardPan = txnInfo.cardPAN, cardExpiry = txnInfo.cardExpiry, cardType = "",
                            stan = response.stan, pinStatus = pinStatus, AID = emvData.AID, code = "",
                            telephone = "08031150978")

                    // complete transaction by applying scripts
                    // only when responseCode is 'OK'
                    if(it.responseCode == IsoUtils.OK) {
                        val completionResult = emv.completeTransaction(it)

                        when (completionResult) {
                            EmvResult.OFFLINE_APPROVED -> logger.log("online has been approved")
                            else -> {
                                toast("Transaction Declined")
                                showContainer(CardTransactionState.Default)
                            }
                        }

                    }

                    // show transaction result screen
                    showTransactionResult(txn)
                } ?: toast("Unable to process Transaction").also { finish() }

            } else {
                toast("Unable to getResult icc").also { finish() }
            }
        } ?: toast("No terminal info, found on device").also { finish() }
    }


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
            runOnUiThread { paymentHint.text = getString(R.string.isw_hint_input_pin) }
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
