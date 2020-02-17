package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.utilities.DialogUtils
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_layout_insert_supervisors_card.*
import kotlinx.android.synthetic.main.isw_sheet_layout_admin_merchant_card.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MerchantCardDialog constructor(
    private val clickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog() {

//    private val cardViewModel by viewModel<CardViewModel>()


    private val cardViewModel: CardViewModel by viewModel()
    private val store by inject<KeyValueStore>()
    val terminalInfo by lazy { TerminalInfo.get(store)!! }
    private var isEnrollment:Boolean=false


    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_admin_merchant_card


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ISW_FullScreenDialogStyleTransparent)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        cardViewModel.emvMessage.observe(this, Observer {
//            it?.let(::processMessage)
//        })
 //      cardViewModel.setupTransaction(0, terminalInfo)
        if (!IswPos.hasFingerprint()) {
            isw_use_fingerprint.visibility = View.GONE
        }
//
        isw_use_fingerprint.setOnClickListener {
            clickListener.invoke(USE_FINGERPRINT)
            dismiss()
        }


//        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
//        mBehavior.setPeekHeight(your dialog height)

//        setStyle(DialogFragment.STYLE_NORMAL,R.style.ISW_FullScreenDialogStyle)
        authFunction()
    }






    private fun authFunction(){
//        cardViewModel.emvMessage.observe(this,  androidx.lifecycle.Observer {
//            it?.let(::processMessage)
//        })

        // observe view model
        isw_pin.text=""
        isw_enrollmentLabel.text="Enroll Supervisor's Card"

        if(!isEnrollment  ) {

            isw_enrollmentLabel.text="Authenticate Supervisor's Card"
            val savedPan = store.getString("M3RCHANT_PAN", "")
            if (savedPan==""){

                clickListener.invoke(NOT_ENROLLED)
                dismiss()
                return
            }
        }
else if(terminalInfo== null){
            clickListener.invoke(NOT_ENROLLED)
            dismiss()
            return
        }



        observeViewModel()
        cardViewModel.setupTransaction(1, terminalInfo)
        if (!IswPos.hasFingerprint()) {
            //  isw_use_fingerprint.visibility = View.GONE
        }

//        isw_use_fingerprint.setOnClickListener {
//            clickListener.invoke(USE_FINGERPRINT)
//            dismiss()
    }




    private fun observeViewModel() {
        with(cardViewModel) {

            val owner = { lifecycle }

            // observe emv messages
            emvMessage.observe(owner) {
                it?.let(::processMessage)
            }

            // observe transaction response
//            transactionResponse.observe(owner) {
//                it?.let(::processResponse)
//            }

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

    private val alert by lazy {
        DialogUtils.getAlertDialog(requireContext())
                .setTitle("Invalid Configuration")
                .setMessage("The configuration contains invalid parameters, please fix the errors and try saving again")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
    }

    private var cardType = CardType.None
    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
//                showLoader("Reading Card", "Loading...")
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {

            }

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
//                dialog.dismiss()

                cardType = message.cardType

                //Show Card detected view
//                showCardDetectedView()

                //TODO: Uncomment this, was commented out during conflict resolution
                cardViewModel.startTransaction(requireContext())


            }

            is EmvMessage.CardDetails -> cardType = message.cardType

            // when card gets removed
            is EmvMessage.CardRemoved -> {
//                showInsertCardView()
//                cancelTransaction("Transaction Cancelled: Card was removed")
            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {
//                if (paymentModel.type == PaymentModel.TransactionType.CARD_PURCHASE) accountTypeDialog.dismiss()
//                iswCardPaymentViewAnimator.displayedChild = 1
//                isw_amount.text = paymentModel.formattedAmount
                context?.toast("Enter your pin")

                isw_enrollmentLabel.text="Enter Pin"
            }

            // when user types in pin
            is EmvMessage.PinText -> {
                // cardPin.setText(message.text)
                isw_pin.setText(message.text)

            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
                println("Called PIN OKAY")
//                pinOk = true


                print(cardViewModel.getCardPAN())
//                context?.toast(+"")
                context?.toast("Pin OK")

               var subStringPan = cardViewModel.getCardPAN()?.substring(6);

                if(!isEnrollment){
                    val savedPan = store.getString("M3RCHANT_PAN", "")

                    if (savedPan == subStringPan) {
                        clickListener.invoke(AUTHORIZED)
                        dismiss()
                    } else {
                        clickListener.invoke(FAILED)
                        dismiss()
                    }
                }
                else{
                    //during validation
                    store.saveString("M3RCHANT_PAN", subStringPan+"")
                    clickListener.invoke(AUTHORIZED)
                }

                dismiss()


            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {

                alert.setTitle("Invalid Pin")
                alert.setMessage("Please press the CANCEL (X) button and try again")
                alert.show()
            }

            // when the user enters an incomplete pin
            is EmvMessage.EmptyPin -> {

                alert.setTitle("Empty Pin")
                alert.setMessage("Please press the CANCEL (X) button and try again")
                alert.show()
            }

            // when pin is incorrect
            is EmvMessage.PinError -> {
                alert.setTitle("Invalid Pin")
                alert.setMessage("Please ensure you put the right pin.")
                alert.show()

//                val isPosted = Handler().postDelayed({ alert.dismiss() }, 3000)
            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {
//                cancelTransaction(message.reason)

                context?.toast(message.reason)
                // remove dialogs
                dismiss()
            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {
//                val direction = CardTransactionsFragmentDirections.iswActionGotoFragmentProcessingTransaction(
//                        paymentModel, transactionType, cardType, accountType, paymentInfo
//                )
//                navigate(direction)
            }
        }
   }

    fun setIsEnrollment(b: Boolean) {
        isEnrollment=b
    }

    companion object {
        const val AUTHORIZED = 0
        const val FAILED = 1
        const val USE_FINGERPRINT = 2
        const val NOT_ENROLLED = 3
        const val TAG = "Merchant Card Dialog"
    }
}