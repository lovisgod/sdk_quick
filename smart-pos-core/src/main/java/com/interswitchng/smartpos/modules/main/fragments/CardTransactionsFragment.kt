package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.dialogs.AccountTypeDialog
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.modules.main.models.TransactionResultModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.EmvData
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.PurchaseType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.TransactionInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.response.TransactionResponse
import com.interswitchng.smartpos.shared.services.iso8583.utils.IsoUtils
import com.interswitchng.smartpos.shared.utilities.*
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.isw_activity_card.cardPin
import kotlinx.android.synthetic.main.isw_fragment_card_payment.*
import kotlinx.android.synthetic.main.isw_fragment_pin.*
import kotlinx.android.synthetic.main.isw_layout_card_found.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class CardTransactionsFragment : BaseFragment(TAG) {

    private var accountType = AccountType.Default
    private var cardType = CardType.None
    private lateinit var transactionResult: TransactionResultModel
    private var pinOk = false
    private var isCancelled = false

    private lateinit var accountTypeDialog: AccountTypeDialog
    private lateinit var paymentTypeDialog: PaymentTypeDialog
    private val dialog by lazy { DialogUtils.getLoadingDialog(context!!) }
    private val alert by lazy { DialogUtils.getAlertDialog(context!!).create() }


    private val cardViewModel: CardViewModel by viewModel()

    private val cardPaymentFragmentArgs by navArgs<CardTransactionsFragmentArgs>()
    private val paymentModel by lazy { cardPaymentFragmentArgs.PaymentModel }

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan())
    }

    private val cancelDialog by lazy {
        DialogUtils.getAlertDialog(context!!)
            .setMessage("Would you like to change payment method, or try again?")
            .setCancelable(false)
            .setNegativeButton(R.string.isw_title_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.isw_action_change) { dialog, _ ->
                dialog.dismiss()
                showPaymentOptions()
            }
            .setNeutralButton(R.string.isw_title_try_again) { dialog, _ ->
                dialog.dismiss()
                resetTransaction()
            }.create()
    }

    override val layoutId: Int
        get() = R.layout.isw_fragment_card_payment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (IswPos.isConfigured()) {
            setTransactionType()
            HandleClicks()
            observeViewModel()
            cardViewModel.setupTransaction(paymentInfo.amount, terminalInfo)
        } else {
            context?.toast("POS is not configured")
        }
    }

    private fun setTransactionType() {
        when (paymentModel.type) {
            PaymentModel.TransactionType.PRE_AUTHORIZATION -> {
                cardViewModel.setTransactionType(PaymentModel.TransactionType.PRE_AUTHORIZATION)
            }

            PaymentModel.TransactionType.CARD_PURCHASE -> {
                cardViewModel.setTransactionType(PaymentModel.TransactionType.CARD_PURCHASE)
            }

            PaymentModel.TransactionType.COMPLETION -> {
                cardViewModel.setTransactionType(PaymentModel.TransactionType.COMPLETION)
            }
        }
    }

    private fun HandleClicks() {

        // show PaymentTypeDialog and listen for clicks
        showPaymentOptions()

        isw_card_found_continue.setOnClickListener {
            when (paymentModel.type) {
                PaymentModel.TransactionType.CARD_PURCHASE -> {
                    accountTypeDialog = AccountTypeDialog {
                        accountType = when (it) {
                            0 -> AccountType.Default
                            1 -> AccountType.Savings
                            2 -> AccountType.Current
                            else -> AccountType.Default
                        }

                        runWithInternet {
                            cardViewModel.startTransaction(
                                context!!,
                                paymentInfo,
                                accountType,
                                terminalInfo
                            )
                        }
                    }
                    accountTypeDialog.show(childFragmentManager, TAG)
                }

                else -> {
                    runWithInternet {
                        cardViewModel.startTransaction(
                            context!!,
                            paymentInfo,
                            accountType,
                            terminalInfo
                        )
                    }
                }
            }
        }

        isw_toolbar.setNavigationOnClickListener {
            navigateUp()
        }
    }

    private fun showPaymentOptions() {
        change_payment_method.setOnClickListener {
            paymentTypeDialog = PaymentTypeDialog(PaymentModel.PaymentType.CARD) {
                when (it) {
                    PaymentModel.PaymentType.CARD -> {}
                    PaymentModel.PaymentType.PAY_CODE -> {
                        Toast.makeText(context, "Paycode", Toast.LENGTH_LONG)}
                    PaymentModel.PaymentType.QR_CODE -> {}
                    PaymentModel.PaymentType.USSD -> {}
                }

            }
            paymentTypeDialog.show(childFragmentManager, TAG)
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
                            //showContainer(CardTransactionState.Default)
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_APPROVED -> {
                            context?.toast("Transaction Approved")
                        }
                    }
                }
            }
        }
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
                showLoader("Reading Card", "Loading...")
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {

            }

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
                dialog.dismiss()

                cardType = message.cardType

                //Show Card detected view
                showCardDetectedView()
            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {
                showInsertCardView()
                cancelTransaction("Transaction Cancelled: Card was removed")
            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {
                if (paymentModel.type == PaymentModel.TransactionType.CARD_PURCHASE) accountTypeDialog.dismiss()
                iswCardPaymentViewAnimator.displayedChild = 1
                isw_amount.text = paymentModel.formattedAmount
                context?.toast("Enter your pin")
            }

            // when user types in pin
            is EmvMessage.PinText -> {
                cardPin.setText(message.text)
            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
                println("Called PIN OKAY")
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

                // show transaction progress alert
                showProgressAlert(false)
            }
        }
    }

    private fun showInsertCardView() {
        isw_card_found.hide()
        isw_scanning_card.show()
    }


    private fun processResponse(transactionResponse: Optional<Pair<TransactionResponse, EmvData>>) {

        when (transactionResponse) {
            is None -> logger.log("Unable to complete transaction")
            is Some -> {
                // extract info
                val response = transactionResponse.value.first
                val emvData = transactionResponse.value.second
                val txnInfo =
                    TransactionInfo.fromEmv(emvData, paymentInfo, PurchaseType.Card, accountType)

                val responseMsg = IsoUtils.getIsoResultMsg(response.responseCode) ?: "Unknown Error"
                val pinStatus = when {
                    pinOk || response.responseCode == IsoUtils.OK -> "PIN Verified"
                    else -> "PIN Unverified"
                }

                val now = Date()
                transactionResult = TransactionResultModel(
                    paymentType = PaymentType.Card,
                    dateTime = DisplayUtils.getIsoString(now),
                    amount = DisplayUtils.getAmountString(paymentInfo),
                    authorizationCode = response.authCode,
                    responseMessage = responseMsg,
                    responseCode = response.responseCode,
                    cardPan = txnInfo.cardPAN, cardExpiry = txnInfo.cardExpiry, cardType = cardType,
                    stan = response.stan, pinStatus = pinStatus, AID = emvData.AID, code = "",
                    telephone = iswPos.config.merchantTelephone
                )

                dismissAlert()

                val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentReceipt(
                    TransactionResponseModel(transactionResult = transactionResult,
                        transactionType = PaymentModel.TransactionType.CARD_PURCHASE)
                )
                navigate(direction)
            }
        }
    }

    private fun showCardDetectedView() {
        //Hide Scanning Card View
        isw_scanning_card.hide()

        //Show Card Detected View
        isw_card_found.show()

        when (paymentModel.type) {
            PaymentModel.TransactionType.CARD_PURCHASE ->  isw_change_payment_method_group.visibility = View.VISIBLE
            else -> isw_change_payment_method_group.visibility = View.GONE
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
        cancelDialog.setTitle(reason)
        if (!cancelDialog.isShowing) cancelDialog.show()
    }

    private fun resetTransaction() {
        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
    }

    private fun showLoader(title: String = "Processing", message: String) {
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.show()
    }

    companion object {
        const val TAG = "Card Transaction"
    }

}