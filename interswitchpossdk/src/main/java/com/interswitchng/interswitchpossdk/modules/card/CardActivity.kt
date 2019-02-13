package com.interswitchng.interswitchpossdk.modules.card

import android.app.Activity
import android.os.Bundle
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.modules.card.model.CardTransactionState
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.shared.interfaces.library.EmvCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IKeyValueStore
import com.interswitchng.interswitchpossdk.shared.interfaces.library.IsoService
import com.interswitchng.interswitchpossdk.shared.models.TerminalInfo
import com.interswitchng.interswitchpossdk.shared.models.transaction.TransactionResult
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.interswitchpossdk.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import com.interswitchng.interswitchpossdk.shared.utilities.Logger
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.content_account_options.*
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class CardActivity : BaseActivity() {

    private val logger by lazy { Logger.with("CardActivity") }

    private val printer by lazy { posDevice.printer }
    private val emvCallback by lazy { CardCallback() }
    private val emv by lazy { posDevice.getEmvCardTransaction() }

    private val isoService: IsoService by inject()
    private val store: IKeyValueStore by inject()
    private lateinit var accountType: AccountType

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        // setup toolbar
        setupToolbar("Card")

        // set the amount
        val amount = NumberFormat.getInstance().format(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)
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
        savingsAccount.setOnClickListener {
            accountType = AccountType.Savings
            startTransaction()
        }
        currentAccount.setOnClickListener {
            accountType = AccountType.Current
            startTransaction()
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
                showContainer(CardTransactionState.ChooseAccountType)
            }

        }.start()
    }

    private fun startTransaction() {
        Thread {
            // start card transaction
            val result = emv.startTransaction()

            when (result) {
                TransactionResult.OFFLINE_APPROVED -> emv.completeTransaction()
                TransactionResult.ONLINE_REQUIRED -> logger.log("online should be processed").also { processOnline() }
                TransactionResult.OFFLINE_DENIED -> {
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
            CardTransactionState.ChooseAccountType -> accountOptionsContainer
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
        TerminalInfo.get(store)?.let { terminalInfo ->
            val emv = emv.getTransactionInfo()

            if (emv != null) {
                val transactionInfo = TransactionInfo.fromEmv(emv, paymentInfo.amount, PurchaseType.Card, accountType)
                isoService.initiatePurchase(terminalInfo, transactionInfo)
            } else {
                toast("Unable to get icc")
            }
        } ?: toast("No terminal info, found on device")
    }

    internal inner class CardCallback : EmvCallback {

        override fun showInsertCard() {
            showContainer(CardTransactionState.ShowInsertCard)
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
