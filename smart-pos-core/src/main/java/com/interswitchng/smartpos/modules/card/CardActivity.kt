package com.interswitchng.smartpos.modules.card

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.model.CardTransactionState
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.interfaces.library.EmvCallback
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.Logger
import com.interswitchng.smartpos.shared.utilities.ThreadUtils
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.isw_activity_card.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import java.util.*

class CardActivity : BaseActivity() {

    private val logger by lazy { Logger.with("CardActivity") }

    private val emvCallback by lazy { CardCallback() }
    private val emv by lazy { posDevice.getEmvCardTransaction() }

    private val isoService: IsoService by inject()
    private val store: KeyValueStore by inject()
    private var accountType = AccountType.Default
    private var cardType = CardType.None
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false
    private var isCancelled = false



    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private val alert by lazy { DialogUtils.getAlertDialog(this).create() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.isw_activity_card)

        // set the amount
        val amount = DisplayUtils.getAmountString(paymentInfo)
        amountText.text = getString(R.string.isw_amount, amount)
    }

    override fun onStart() {
        super.onStart()
        setupTransaction()
        isCancelled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isCancelled = true
        emv.removeEmvCallback(emvCallback)
        emv.cancelTransaction()
    }


    private fun setupTransaction() {
        val disposable = ThreadUtils.createExecutor {

            // attach callback for emv transaction
            emv.setEmvCallback(emvCallback)

            TerminalInfo.get(store)?.let {

                // setup card transaction
                emv.setupTransaction(paymentInfo.amount, it)

                runOnUiThread { dialog.dismiss() }

                // show account type selection
                chooseAccount()
            }
        }

        disposables.add(disposable)
    }

    private fun chooseAccount() = runOnUiThread {
        // check if transaction was cancelled
        if (!isCancelled) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.isw_hint_account_type)
                    .setCancelable(false)
                    .setSingleChoiceItems(R.array.isw_account_types, accountType.ordinal) { dialog, selectedIndex ->
                        if (selectedIndex != -1) {
                            // set the selected account
                            accountType = when (selectedIndex) {
                                1 -> AccountType.Savings
                                2 -> AccountType.Current
                                3 -> AccountType.Credit
                                else -> AccountType.Default
                            }

                            selectedCardType.text = accountType.toString()

                            // start transaction
                            startTransaction()

                            //
                        }
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    private fun startTransaction() {
        val disposable = ThreadUtils.createExecutor {
            // start card transaction
            val result = emv.startTransaction()

            when (result) {
                EmvResult.ONLINE_REQUIRED -> logger.log("online should be processed").also { processOnline() }
                else -> runOnUiThread {
                    if (!isCancelled) {
                        toast("Error processing card transaction")
                        cancelTransaction("Unable to process card transaction")
                    }
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
        val container = when (state) {
            CardTransactionState.ShowInsertCard -> insertCardContainer
            CardTransactionState.EnterPin -> insertPinContainer
            CardTransactionState.Default -> blankContainer
        }

        runOnUiThread { container.bringToFront() }
    }

    private fun cancelTransaction(reason: String) {
        runOnUiThread {
            // remove dialogs
            if (dialog.isShowing) dialog.dismiss()
            if (alert.isShowing) alert.dismiss()
            // set flag
            isCancelled = true

            val cancel = {
                // set cancel result
                setResult(Activity.RESULT_CANCELED)
                toast(reason)
                finish()
            }


            DialogUtils.getAlertDialog(this)
                    .setTitle(reason)
                    .setMessage("Would you like to change payment method, or try again?")
                    .setNegativeButton(R.string.isw_title_cancel) { dialog, _ ->
                        dialog.dismiss()
                        cancel()
                    }
                    .setPositiveButton(R.string.isw_action_change) { dialog, _ ->
                        dialog.dismiss()
                        showPaymentOptions(BottomSheetOptionsDialog.CARD)
                    }
                    .setNeutralButton(R.string.isw_title_try_again) { dialog, _ ->
                        dialog.dismiss()
                        resetTransaction()
                    }
                    .show()
        }
    }

    private fun processOnline() {
        runOnUiThread {
            // change hint text
            paymentHint.text = getString(R.string.isw_title_processing_transaction)
            // hide other layouts: show default screen
            showContainer(CardTransactionState.Default)
            // show transaction progress alert
            showProgressAlert(false)
        }

        // get emv data captured by card
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
                val pinStatus = when {
                    pinOk || it.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                transactionResult = TransactionResult(
                        paymentType = PaymentType.Card,
                        dateTime = DisplayUtils.getIsoString(now),
                        amount = DisplayUtils.getAmountString(paymentInfo),
                        type = TransactionType.Purchase,
                        authorizationCode = response.authCode,
                        responseMessage = responseMsg,
                        responseCode = response.responseCode,
                        cardPan = txnInfo.cardPAN, cardExpiry = txnInfo.cardExpiry, cardType = cardType,
                        stan = response.stan, pinStatus = pinStatus, AID = emvData.AID, code = "",
                        telephone =  "08031150978")

                // complete transaction by applying scripts
                // only when responseCode is 'OK'
                if (it.responseCode == IsoUtils.OK) {
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
    }

    private fun resetTransaction() {
        val newIntent = Intent(intent)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        startActivity(newIntent)
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

        override fun onCardRead(cardType: CardType) = runOnUiThread {
            this@CardActivity.cardType = cardType
            val cardIcon = when (cardType) {
                CardType.MASTER -> R.drawable.isw_ic_card_mastercard
                CardType.VISA -> R.drawable.isw_ic_card_visa
                else -> R.drawable.isw_ic_card
            }

            // set the card icon
            cardTypeIcon.setImageResource(cardIcon)
        }

        override fun showEnterPin() = runOnUiThread {
            showContainer(CardTransactionState.EnterPin)
            paymentHint.text = getString(R.string.isw_hint_input_pin)
        }

        override fun setPinText(text: String) = runOnUiThread {
            cardPin.setText(text)
        }

        override fun showPinOk() = runOnUiThread {
            pinOk = true
            toast("Pin OK")
        }


        override fun onCardRemoved() {
            cancelTransaction("Transaction Cancelled: Card was removed")
        }

        override fun onTransactionCancelled(code: Int, reason: String) {
            cancelTransaction(reason)
        }

        override fun showPinError(remainCount: Int) = runOnUiThread {
            alert.setTitle("Invalid Pin")
            alert.setMessage("Please ensure you put the right pin.")
            alert.show()
        }

    }
}
