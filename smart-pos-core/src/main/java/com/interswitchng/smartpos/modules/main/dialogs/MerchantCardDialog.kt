package com.interswitchng.smartpos.modules.main.dialogs

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.shared.activities.BaseBottomSheetDialog
import com.interswitchng.smartpos.shared.interfaces.library.KeyValueStore
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.utilities.SingleArgsClickListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MerchantCardDialog constructor(
    private val clickListener: SingleArgsClickListener<Boolean>
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
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {

            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {}

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
                cardViewModel.readCardPan()
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
                val savedPan = store.getString("M3RCHANT_PAN", "")
                if (savedPan == cardViewModel.getCardPAN()) {
                    clickListener.invoke(true)
                    dismiss()
                } else {
                    clickListener.invoke(false)
                    dismiss()
                }
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
}