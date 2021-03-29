package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.dialogs.AccountTypeDialog
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.fragments.CardTransactionsFragmentArgs
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.transfer.customdailog
import com.interswitchng.smartpos.modules.main.transfer.showErrorDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.posconfig.PosType
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentInfo
import com.interswitchng.smartpos.shared.models.transaction.TransactionResult
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.AccountType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.request.OriginalTransactionInfoData
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.hide
import com.interswitchng.smartpos.shared.utilities.show
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_card_payment.*
import kotlinx.android.synthetic.main.isw_fragment_card_payment.iswCardPaymentViewAnimator
import kotlinx.android.synthetic.main.isw_fragment_pin.*
import kotlinx.android.synthetic.main.isw_fragment_pin.cardPin
import kotlinx.android.synthetic.main.isw_fragment_transfer_card_transaction.*
import kotlinx.android.synthetic.main.isw_layout_card_found.*
import org.koin.android.viewmodel.ext.android.viewModel

class TransferCardTransactionFragment : BaseFragment(TAG) {
    private var accountType = AccountType.Default
    private var cardType = CardType.None
    private lateinit var transactionResult: TransactionResult
    private var pinOk = false
    private var isCancelled = false
    private lateinit var transactionType: TransactionType
    private val deviceName = IswPos.getInstance().device.name

    private lateinit var accountTypeDialog: AccountTypeDialog
    private lateinit var paymentTypeDialog: PaymentTypeDialog
    private val dialog by lazy { DialogUtils.getLoadingDialog(context!!) }
    private val alert by lazy { DialogUtils.getAlertDialog(context!!).create() }
    private lateinit var loading: Dialog

    private val cardViewModel: CardViewModel by viewModel()

    private val cardPaymentFragmentArgs by navArgs<TransferCardTransactionFragmentArgs>()
    private val paymentModel by lazy { cardPaymentFragmentArgs.paymentModel }

    private val paymentInfo by lazy {
        PaymentInfo(paymentModel.amount, IswPos.getNextStan(), paymentModel.stan, paymentModel.authorizationId)
    }

    private val originalTxnData by lazy {
        paymentModel.originalDateAndTime?.let { timeDate ->
            paymentModel.originalStan?.let {
                stan ->
                OriginalTransactionInfoData(originalStan = stan,
                        originalTransmissionDateAndTime = timeDate, time = -1L)
            }
        }
    }

    private val cancelDialog by lazy {

        DialogUtils.getAlertDialog(context!!)
                .setMessage("Remove card")
                .setCancelable(false)
                .setPositiveButton(R.string.isw_title_cancel) { dialog, _ ->
                    dialog.dismiss()
                    //goBackToPreviousFragment()
                    findNavController().popBackStack()
                }.create()
    }



    override val layoutId: Int
        get()  = R.layout.isw_fragment_transfer_card_transaction


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = Dialog(this.requireContext())
        if (IswPos.isConfigured()) {
            setTransactionType()
            observeViewModel()
            isw_card_found_continue.setOnClickListener {
                showAccountTypeDialog()
            }
            cardViewModel.setupTransaction(paymentInfo.amount, terminalInfo)
        } else {
            context?.toast("POS is not configured")
        }
    }


    private fun observeViewModel() {
        with(cardViewModel) {

            val owner = { lifecycle }

            // observe emv messages
            emvMessage.observe(owner) {
                it?.let(::processMessage)
            }


            // observe online process results
            onlineResult.observe(owner) {
                it?.let { result ->
                    when (result) {
                        CardViewModel.OnlineProcessResult.NO_EMV -> {
                            context?.toast("Unable to getResult icc")
                            showErrorDialog(this@TransferCardTransactionFragment.requireContext()
                                    , "Unable to getResult icc")
                            //finish()
                        }
                        CardViewModel.OnlineProcessResult.NO_RESPONSE -> {
                            context?.toast("Unable to process Transaction")
                            showErrorDialog(this@TransferCardTransactionFragment.requireContext()
                                    , "Unable to process Transaction")
                            //finish()
                        } 
                        CardViewModel.OnlineProcessResult.ONLINE_DENIED -> {
                            context?.toast("Transaction Declined")
                            showErrorDialog(this@TransferCardTransactionFragment.requireContext()
                                    , "Transaction Declined")

                            //showContainer(CardTransactionState.Default)
                        }
                        CardViewModel.OnlineProcessResult.ONLINE_APPROVED -> {
                            context?.toast("Transaction Approved")
                            showErrorDialog(this@TransferCardTransactionFragment.requireContext()
                                    , "Transaction Approved")
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
                logger.log("me: CardDetected")
                if(deviceName == PosType.PAX.name) {
                    loading = customdailog(context = this.requireContext(), message = "Reading card")
//                    showLoader("Reading Card", "Loading...")
                } else {
                    // do nothing
                }
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {

            }

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
                dialog.dismiss()
                loading.dismiss()

                cardType = message.cardType
//                CardTransactionsFragment.CompletionData.cardType = message.cardType

                //Show Card detected view
                showCardDetectedView()
            }

            is EmvMessage.CardDetails -> {
                cardType = message.cardType
//                CardTransactionsFragment.CompletionData.cardType = message.cardType
            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {
                showInsertCardView()
                if(deviceName == PosType.PAX.name) {
                    cancelTransaction("Transaction Cancelled: Card was removed")
                }
                context?.toast("Transaction Cancelled: Card was removed")
            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {
                println(message)
                loading.dismiss()
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

            // when the user enters an incomplete pin
            is EmvMessage.EmptyPin -> {
                println("here here$message")
                alert.setTitle("Empty Pin")
                alert.setMessage("Please press the CANCEL (X) button and try again")
                alert.show()
            }

            // when pin is incorrect
            is EmvMessage.PinError -> {
                println("here here here$message")
                alert.setTitle("Invalid Pin")
                alert.setMessage("Please ensure you put the right pin.")
                alert.show()

                val isPosted = Handler().postDelayed({ alert.dismiss() }, 3000)
            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {
                loading.dismiss()
                cancelTransaction(message.reason)
            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {
                val direction = TransferCardTransactionFragmentDirections
                        .iswActionIswTransfercardtransactionfragmentToIswTransfertransactionpreocessingfragment(
                                paymentModel, transactionType, cardType, accountType, paymentInfo
                        )
                findNavController().navigate(direction)
            }
        }
    }

    private fun showInsertCardView() {
        isw_card_found_transfer.hide()
        isw_scanning_card_transfer.show()
    }

    private fun setTransactionType() {
        println(originalTxnData)
//        cardViewModel.setOriginalTxnInfo(originalTxnData!!)
    if (paymentModel.paymentType == PaymentModel.TransactionType.BILL_PAYMENT) {

    }
        cardViewModel.setTransactionType(PaymentModel.TransactionType.TRANSFER)
        transactionType = TransactionType.Transfer
    }


    private fun showCardDetectedView() {
        //Hide Scanning Card View
        isw_scanning_card_transfer.hide()

        //Show Card Detected View
        isw_card_found_transfer.show()

        //Show account dialog
        showAccountTypeDialog()

//        when (paymentModel.type) {
//            PaymentModel.TransactionType.CARD_PURCHASE ->  isw_change_payment_method_group.visibility = View.VISIBLE
//            else -> isw_change_payment_method_group.visibility = View.GONE
//        }
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
        // cancel the transaction
        cardViewModel.cancelTransaction()
    }

    private fun showLoader(title: String = "Processing", message: String) {
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.show()
    }


    private fun showAccountTypeDialog() {
        accountTypeDialog = AccountTypeDialog {
            accountType = when (it) {
                0 -> AccountType.Default
                1 -> AccountType.Savings
                2 -> AccountType.Current
                else -> AccountType.Default
            }

            runWithInternet {
                cardViewModel.startTransaction(requireContext())
            }
        }
        accountTypeDialog.show(childFragmentManager, TAG)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferCardTransactionFragment()
        val TAG = "TRANSFER CARD TRANSACTION FRAGMENT"
    }
}