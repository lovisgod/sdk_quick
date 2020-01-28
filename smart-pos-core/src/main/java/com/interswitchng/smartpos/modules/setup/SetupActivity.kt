package com.interswitchng.smartpos.modules.setup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.card.CardViewModel
import com.interswitchng.smartpos.modules.main.MainActivity
import com.interswitchng.smartpos.shared.models.core.TerminalInfo
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.EmvMessage
import com.interswitchng.smartpos.shared.utilities.toast
import kotlinx.android.synthetic.main.isw_fragment_pin.*
import kotlinx.android.synthetic.main.isw_fragment_setup.*
import kotlinx.android.synthetic.main.isw_layout_phone_number.*
import kotlinx.android.synthetic.main.isw_layout_supervisors_card_pin.*
import kotlinx.android.synthetic.main.isw_layout_supervisors_card_pin.isw_card_pan
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel

class SetupActivity : AppCompatActivity() {

    private val setupViewModel: SetupFragmentViewModel by viewModel()
    private val cardViewModel: CardViewModel by viewModel()

    private val terminalInfo: TerminalInfo by lazy { TerminalInfo.get(get())!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (setupViewModel.isSetup()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        setContentView(R.layout.isw_fragment_setup)
        setupViews()
        cardViewModel.emvMessage.observe(this, Observer {
            it?.let(::processMessage)
        })
        cardViewModel.setupTransaction(0, terminalInfo)
    }

    private fun setupViews() {
        isw_get_started.setOnClickListener {
            val pan = cardViewModel.getCardPAN()!!
            setupViewModel.saveMerchantPAN(pan)
            isw_enter_pin_layout.visibility = View.GONE
            isw_enter_phone_number.visibility = View.VISIBLE
        }
        isw_save_phone_number.setOnClickListener {
            val number = isw_merchant_number.text.toString()
            if (!Patterns.PHONE.matcher(number).matches()) {
                toast("Invalid Phone Number Type")
                return@setOnClickListener
            }
            setupViewModel.saveMerchantPhoneNumber(number)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun processMessage(message: EmvMessage) {

        // assigns value to ensure the when expression is exhausted
        val ignore = when (message) {

            // when card is detected
            is EmvMessage.CardDetected -> {
                isw_insert_card_layout.visibility = View.GONE
                isw_enter_pin_layout.visibility = View.GONE
                isw_card_detected_layout.visibility = View.VISIBLE
                isw_imageview.visibility = View.VISIBLE
            }

            // when card should be inserted
            is EmvMessage.InsertCard -> {
                isw_insert_card_layout.visibility = View.VISIBLE
                isw_enter_pin_layout.visibility = View.GONE
                isw_card_detected_layout.visibility = View.GONE
                isw_imageview.visibility = View.VISIBLE
            }

            // when card has been read
            is EmvMessage.CardRead -> {
                //Dismiss the dialog showing "Reading Card"
                cardViewModel.readCard()
            }

            // when card gets removed
            is EmvMessage.CardRemoved -> {
                isw_insert_card_layout.visibility = View.VISIBLE
                isw_enter_pin_layout.visibility = View.GONE
                isw_card_detected_layout.visibility = View.GONE
                isw_imageview.visibility = View.VISIBLE
            }

            // when user should enter pin
            is EmvMessage.EnterPin -> {

            }

            // when user types in pin
            is EmvMessage.PinText -> {
                cardPin.setText(message.text)
            }

            // when pin has been validated
            is EmvMessage.PinOk -> {
                isw_insert_card_layout.visibility = View.GONE
                isw_enter_pin_layout.visibility = View.VISIBLE
                isw_card_detected_layout.visibility = View.GONE
                isw_imageview.visibility = View.GONE
                isw_card_pan.text = cardViewModel.getCardPAN()
                toast("Pin OK")
            }

            // when the user enters an incomplete pin
            is EmvMessage.IncompletePin -> {

            }

            // when pin is incorrect
            is EmvMessage.PinError -> {

            }

            // when pin is incorrect
            is EmvMessage.EmptyPin -> {

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

