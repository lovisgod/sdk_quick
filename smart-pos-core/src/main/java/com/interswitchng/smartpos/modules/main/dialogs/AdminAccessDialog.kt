package com.interswitchng.smartpos.modules.main.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.viewmodels.FingerprintViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import com.interswitchng.smartpos.shared.utilities.toast
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel

class AdminAccessDialog constructor(
    private val authenticationListener: SingleArgsClickListener<Boolean>
) : BaseBottomSheetDialog() {

    private val fingerprintViewModel: FingerprintViewModel by viewModel()
    private val cardViewModel: CardViewModel by viewModel()

    private val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_admin_access

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        if (!fingerprintViewModel.hasFingerprint(requireParentFragment().requireContext())) {
//            Toast.makeText(sheetContext, "No fingerprint enrolled into the terminal", Toast.LENGTH_LONG).show()
//            authenticationListener.invoke(false)
//            dismiss()
//            return
//        }
//        val startTime = System.currentTimeMillis()
//        Toast.makeText(sheetContext, "Place your fingerprint on the sensor", Toast.LENGTH_LONG).show()
//        while (System.currentTimeMillis() - startTime <= 10_000) {
//            val result = fingerprintViewModel.confirmFinger(sheetContext)
//            hasValidatedFinger = result
//
//            if (result) break
//        }
//        if (!hasValidatedFinger) {
//            Toast.makeText(sheetContext, "Could not validate fingerprint. Please try again!", Toast.LENGTH_LONG).show()
//            authenticationListener.invoke(false)
//            dismiss()
//            return
//        }

        cardViewModel.setupTransaction(0, terminalInfo)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener {
            val sheetDialog = it as BottomSheetDialog
            val bottomSheet: FrameLayout = sheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return dialog
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
                cardViewModel.readCardPan()
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {

            }

            // when card has been read
            is EmvMessage.CardRead -> {

                //Dismiss the dialog showing "Reading Card"
//                dialog.dismiss()
//
//                cardType = message.cardType
//
//                //Show Card detected view
//                showCardDetectedView()
            }

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
//                context?.toast("Enter your pin")
            }

            // when user types in pin
            is EmvMessage.PinText -> {
//                cardPin.setText(message.text)
            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
//                println("Called PIN OKAY")
//                pinOk = true
                context?.toast("Authorized!")
                authenticationListener.invoke(true)
                dismiss()
            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {

//                alert.setTitle("Invalid Pin")
//                alert.setMessage("Please press the CANCEL (X) button and try again")
//                alert.show()
            }

            // when pin is incorrect
            is EmvMessage.PinError -> {
//                alert.setTitle("Invalid Pin")
//                alert.setMessage("Please ensure you put the right pin.")
//                alert.show()
//
//                val isPosted = Handler().postDelayed({ alert.dismiss() }, 3000)
            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {
//                cancelTransaction(message.reason)
            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {

                // show transaction progress alert
//                showProgressAlert(false)
            }
        }
    }

    companion object {
        const val TAG = "Admin Access Dialog"
    }
}