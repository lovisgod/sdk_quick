package com.interswitchng.interswitchpossdk.modules.card

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.interswitchng.interswitchpossdk.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.interfaces.CardInsertedCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.models.response.Transaction
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.content_toolbar.*
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class CardActivity : BaseActivity() {

    private val pos: POSDevice by inject()
    private val cardCallback = CardCallback()

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        // setup toolbar
        setupToolbar("Card")

        // get payment info
        val paymentInfo: PaymentInfo = intent.getParcelableExtra(Constants.KEY_PAYMENT_INFO)

        // set the amount
        val amount = NumberFormat.getInstance().format(paymentInfo.amount)
        amountText.text = getString(R.string.amount, amount)

        // attach callback to detect card
        pos.attachCallback(cardCallback)

        showInsertCard()
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.detachCallback(cardCallback)
    }


    private fun showInsertCard() {
        insertCardContainer.visibility = View.VISIBLE

        btnSubmitPin.isEnabled = false
        btnSubmitPin.setOnClickListener(null)
        insertPinContainer.visibility = View.GONE
    }

    private fun showInputPin() {
        insertCardContainer.visibility = View.GONE

        btnSubmitPin.isEnabled = true
        insertPinContainer.visibility = View.VISIBLE
        btnSubmitPin.setOnClickListener {

            val pin = cardPin.text.toString().trim()
            if (pin.isEmpty()) {
                toast("Enter your pin")
                return@setOnClickListener
            }

            btnSubmitPin.isEnabled = false
            pos.checkPin(pin)
        }
    }

    private fun showLoader(message: String) {
        dialog.setMessage(message)
        dialog.show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onTransactionSuccessful(transaction: Transaction) {}

    internal inner class CardCallback : CardInsertedCallback {

        override fun onCardDetected() {
            runOnUiThread { showLoader("Reading Card") }
        }

        override fun onCardRead(cardDetail: CardDetail) {
            runOnUiThread {
                showInputPin()
                val lastFour = cardDetail.pan.substring(cardDetail.pan.length - 4)
                cardNumber.text = "xxxx-xxxx-xxxx-$lastFour"
                btnSubmitPin.isEnabled = true
            }
        }

        override fun onCardRemoved() {
            TODO("not implemented")
        }

        override fun onError(error: DeviceError) {
            TODO("not implemented")
        }
    }
}
