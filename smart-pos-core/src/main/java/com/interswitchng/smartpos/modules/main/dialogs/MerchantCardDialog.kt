package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_layout_insert_supervisors_card.*
import kotlinx.android.synthetic.main.isw_sheet_layout_admin_merchant_card.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MerchantCardDialog constructor(
        private val isAuthorization: Boolean = false,
        private val clickListener: SingleArgsClickListener<Int>
) : BaseBottomSheetDialog() {

    private val cardViewModel by viewModel<CardViewModel>()
    private val store by inject<KeyValueStore>()
    val terminalInfo by lazy { TerminalInfo.get(store)!! }

    override val layoutId: Int
        get() = R.layout.isw_sheet_layout_admin_merchant_card

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardViewModel.emvMessage.observe(this, Observer {
            it?.let(::processMessage)
        })
        cardViewModel.setupTransaction(0, terminalInfo)

        // ensure device supports finger  print
        val supportsFingerPrint = IswPos.getInstance().device.hasFingerPrintReader
        if (!supportsFingerPrint) {
            isw_use_fingerprint.visibility = View.GONE
        } else {
            isw_use_fingerprint.setOnClickListener {
                clickListener.invoke(USE_FINGERPRINT)
                dismiss()
            }
        }
        if (isAuthorization) {
            //isw_textview17.text = getString(R.string.isw_insert_supervisor_s_card)
        }

        isw_button_pin_proceed.setOnClickListener {

            val savedPin = store.getString("MERCHANT_PIN", "")
            val enteredPin = isw_pin_edit_text.text.toString()

            if (enteredPin == "") {
                context?.toast("Pin Field is empty. Please enter your pin")
            } else if (savedPin == enteredPin) {
                context?.toast("Pin OK")
                clickListener.invoke(AUTHORIZED)
                dismiss()
            } else {
                clickListener.invoke(FAILED)
                dismiss()
            }
        }
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
            }

            is EmvMessage.EmptyPin -> {
            }

            is EmvMessage.CardDetails -> {
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {
            }

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
                //cardViewModel.startTransaction(requireContext())


            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {

            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {

            }

            // when user types in pin
            is EmvMessage.PinText -> {

            }

            // when pin has been validated
            is EmvMessage.PinOk -> {

            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {

            }

            // when pin is incorrect
            is EmvMessage.PinError -> {

            }

            // when user cancels transaction
            is EmvMessage.TransactionCancelled -> {

            }

            // when transaction is processing
            is EmvMessage.ProcessingTransaction -> {

            }
        }
    }

    companion object {
        const val AUTHORIZED = 0
        const val FAILED = 1
        const val USE_FINGERPRINT = 2
        const val TAG = "Merchant Card Dialog"
    }
}