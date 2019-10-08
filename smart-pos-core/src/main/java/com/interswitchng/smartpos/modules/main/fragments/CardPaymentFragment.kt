package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.card.model.CardTransactionState
import com.interswitchng.smartpos.modules.main.dialogs.AccountTypeDialog
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
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
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_activity_card.*
import kotlinx.android.synthetic.main.isw_content_amount.*
import kotlinx.android.synthetic.main.isw_layout_card_found.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CardPaymentFragment : BaseFragment(TAG) {

    private val cardViewModel: CardViewModel by viewModel()

    private val cardPaymentFragmentArgs by navArgs<CardPaymentFragmentArgs>()
    private val paymentModel by lazy { cardPaymentFragmentArgs.PaymentModel }

    private var accountType = AccountType.Default
    private var cardType = CardType.None
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false
    private var isCancelled = false


    private val dialog by lazy { DialogUtils.getLoadingDialog(context!!) }
    private val alert by lazy { DialogUtils.getAlertDialog(context!!).create() }


    override val layoutId: Int
        get() = R.layout.isw_fragment_card_payment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (paymentModel.type) {
            PaymentModel.MakePayment.PRE_AUTHORIZATION -> {
                isw_change_payment_method_group.visibility = View.GONE
            }
            PaymentModel.MakePayment.COMPLETION -> {
                isw_change_payment_method_group.visibility = View.GONE
                isw_detection_text.text = "Completion Detected"
            }
        }
        change_payment_method.setOnClickListener {
            val dialog = PaymentTypeDialog (PaymentModel.PaymentType.CARD) {

            }
            dialog.show(childFragmentManager, TAG)
        }
        isw_card_found_continue.setOnClickListener {
            when (paymentModel.type) {
                PaymentModel.MakePayment.PURCHASE -> {
                    val dialog = AccountTypeDialog {
                        val direction = CardPaymentFragmentDirections.iswActionGotoFragmentPin(paymentModel)
                        navigate(direction)
                    }
                    dialog.show(childFragmentManager, TAG)
                }
                PaymentModel.MakePayment.PRE_AUTHORIZATION -> {
                    val direction = CardPaymentFragmentDirections.iswActionGotoFragmentAmount(paymentModel)
                    navigate(direction)
                }
                else -> {
                    val direction = CardPaymentFragmentDirections.iswActionGotoFragmentPin(paymentModel)
                    navigate(direction)
                }
            }
        }
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
                            context?.toast("Unable to getResult icc")
                            //finish()
                        }
                        CardViewModel.OnlineProcessResult.NO_RESPONSE -> {
                            context?.toast("Unable to process Transaction")
                            //finish()
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_DENIED -> {
                            context?.toast("Transaction Declined")
                            showContainer(CardTransactionState.Default)
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_APPROVED -> {
                            context?.toast("Transaction Approved")
                        }
                    }
                }
            }
        }
    }

    private fun showContainer(state: CardTransactionState) {
        val container = when (state) {
            CardTransactionState.ShowInsertCard -> insertCardContainer
            CardTransactionState.EnterPin -> insertPinContainer
            CardTransactionState.Default -> blankContainer
        }

        container.bringToFront()
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
                //chooseAccount()
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
                context?.toast("Pin OK")
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
                //showProgressAlert(false)
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
                //val txnInfo = TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)

                val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode) ?: "Unknown Error"
                val pinStatus = when {
                    pinOk || response.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                val now = Date()
              /*  transactionResult = TransactionResult(
                    paymentType = PaymentType.Card,
                    dateTime = DisplayUtils.getIsoString(now),
                    amount = DisplayUtils.getAmountString(paymentInfo),
                    type = TransactionType.Purchase,
                    authorizationCode = response.authCode,
                    responseMessage = responseMsg,
                    responseCode = response.responseCode,
                    cardPan = txnInfo.cardPAN, cardExpiry = txnInfo.cardExpiry, cardType = cardType,
                    stan = response.stan, pinStatus = pinStatus, AID = emvData.AID, code = "",
                    telephone = iswPos.config.merchantTelephone)*/


                // show transaction result screen
                //showTransactionResult(Transaction.default())
            }
        }
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
       /* cancelDialog.setTitle(reason)
        if (!cancelDialog.isShowing) cancelDialog.show()*/
    }

    private fun showLoader(title: String = "Processing", message: String) {
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.show()
    }

    companion object {
        const val TAG = "Card Payment"
    }
}