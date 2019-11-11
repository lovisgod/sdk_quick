package com.interswitchng.smartpos.modules.card

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.model.CardTransactionState
import com.interswitchng.smartpos.shared.activities.BaseActivity
import com.interswitchng.smartpos.shared.interfaces.library.IsoService
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.models.transaction.ussdqr.response.Transaction
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.*
import com.interswitchng.smartpos.shared.views.BottomSheetOptionsDialog
import kotlinx.android.synthetic.main.isw_activity_card.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class CardActivity : BaseActivity() {

    private val cardViewModel: CardViewModel by viewModel()

    private val logger by lazy { Logger.with("CardActivity") }
    private val cancelDialog by lazy {
        DialogUtils.getAlertDialog(this)
                .setMessage("Would you like to change payment method, or try again?")
                .setCancelable(false)
                .setNegativeButton(R.string.isw_title_cancel) { dialog, _ ->
                    dialog.dismiss()
                    // set cancel result
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                .setPositiveButton(R.string.isw_action_change) { dialog, _ ->
                    dialog.dismiss()
                    showPaymentOptions(BottomSheetOptionsDialog.CARD)
                }
                .setNeutralButton(R.string.isw_title_try_again) { dialog, _ ->
                    dialog.dismiss()
                    resetTransaction()
                }.create()
    }

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

        // observe view model
        observeViewModel()

        // setup transaction
        cardViewModel.setupTransaction(paymentInfo.amount, terminalInfo)
    }

    override fun onStart() {
        super.onStart()
        isCancelled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isCancelled = true
    }

    private fun chooseAccount() {

        // dismiss dialog
        dialog.dismiss()

        AlertDialog.Builder(this)
                .setTitle(R.string.isw_hint_account_type)
                .setCancelable(false)
                .setSingleChoiceItems(R.array.isw_account_types, -1) { dialog, selectedIndex ->
                    if (selectedIndex != -1) {
                        // set the selected account
                        accountType = when (selectedIndex) {
                            1 -> AccountType.Savings
                            2 -> AccountType.Current
                            3 -> AccountType.Credit
                            else -> AccountType.Default
                        }

                        selectedCardType.text = accountType.toString()

                        // ensure internet connection
                        // before starting transaction
                        runWithInternet {
                            // try starting transaction
                            cardViewModel.startTransaction(this, paymentInfo, accountType, terminalInfo)
                        }
                    }
                    dialog.dismiss()
                }
                .show()
    }

    private fun observeViewModel() {
        with(cardViewModel) {

            val owner = { lifecycle }

            // observe emv messages
            emvMessage.observe(owner) {
                it?.let(::processMessage)
            }

            // observe transaction response
            transactionResponse.observe(owner) {
                it?.let(::processResponse)
            }

            // observe online process results
            onlineResult.observe(owner) {
                it?.let { result ->
                    when (result) {
                        CardViewModel.OnlineProcessResult.NO_EMV -> {
                            toast("Unable to getResult icc")
                            finish()
                        }
                        CardViewModel.OnlineProcessResult.NO_RESPONSE -> {
                            toast("Unable to process Transaction")
                            finish()
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_DENIED -> {
                            toast("Transaction Declined")
                            showContainer(CardTransactionState.Default)
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_APPROVED -> {
                            toast("Transaction Approved")
                        }
                    }
                }
            }
        }
    }


    private fun processResponse(transactionResponse: Optional<Pair<TransactionResponse, EmvData>>) {

        when (transactionResponse) {
            is None -> logger.log("Unable to complete transaction")
            is Some -> {
                // extract info
                val response = transactionResponse.value.first
                val emvData = transactionResponse.value.second
                val txnInfo = TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)

                val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode) ?: "Unknown Error"
                val pinStatus = when {
                    pinOk || response.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                val now = Date()
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
                        telephone = iswPos.config.merchantTelephone)

                println("Called Response code -----> ${response.responseCode}")
                println("Called Response message -----> ${responseMsg}")
                // show transaction result screen
                showTransactionResult(Transaction.default())
            }
        }
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
                showContainer(CardTransactionState.Default)
                showLoader("Reading Card", "Loading...")
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {
                showContainer(CardTransactionState.ShowInsertCard)
                paymentHint.text = getString(R.string.isw_hint_insert_card)
            }

            // when card has been read
            is EmvMessage.CardRead -> {

                cardType = message.cardType
                val cardIcon = when (cardType) {
                    CardType.MASTER -> R.drawable.isw_ic_card_mastercard
                    CardType.VISA -> R.drawable.isw_ic_card_visa
                    CardType.VERVE -> R.drawable.isw_ic_card_verve
                    else -> R.drawable.isw_ic_card
                }

                // set the card icon
                cardTypeIcon.setImageResource(cardIcon)

                // show account type selection
                chooseAccount()
            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {
                cancelTransaction("Transaction Cancelled: Card was removed")
            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {
                showContainer(CardTransactionState.EnterPin)
                cardPin.setText("")
                paymentHint.text = getString(R.string.isw_hint_input_pin)
            }

            // when user types in pin
            is EmvMessage.PinText -> {
                cardPin.setText(message.text)
            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
                pinOk = true
                toast("Pin OK")
            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {

                alert.setTitle("Invalid Pin")
                alert.setMessage("Please press the CANCEL (X) button and try again")
                alert.show()
            }

            // when pin is incorrect
            is EmvMessage.PinError -> {
                alert.setTitle("Invalid Pin")
                alert.setMessage("Please ensure you put the right pin.")
                alert.show()

                val isPosted = Handler().postDelayed({ alert.dismiss() }, 3000)
            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {
                cancelTransaction(message.reason)
            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {
                // change hint text
                paymentHint.text = getString(R.string.isw_title_processing_transaction)
                // hide other layouts: show default screen
                showContainer(CardTransactionState.Default)
                // show transaction progress alert
                showProgressAlert(false)
            }
        }
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

         container.bringToFront()
    }

    private fun cancelTransaction(reason: String) {
        // return early if already cancelled
        if (isCancelled) return

        // remove dialogs
        if (dialog.isShowing) dialog.dismiss()
        if (alert.isShowing) alert.dismiss()
        // set flag
        isCancelled = true

        // set reason and show cancel dialog
        cancelDialog.setTitle(reason)
        if (!cancelDialog.isShowing) cancelDialog.show()
    }

    private fun resetTransaction() {
        val newIntent = Intent(intent)
        newIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        startActivity(newIntent)
    }

    override fun getTransactionResult(transaction: Transaction): TransactionResult? = transactionResult

}
