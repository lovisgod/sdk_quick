package com.interswitchng.interswitchpossdk.modules.card

import android.os.Bundle
import android.widget.Toast
import com.interswitchng.interswitchpossdk.shared.activities.BaseActivity
import com.interswitchng.interswitchpossdk.R
import com.interswitchng.interswitchpossdk.modules.card.model.CardTransactionState
import com.interswitchng.interswitchpossdk.shared.Constants
import com.interswitchng.interswitchpossdk.shared.errors.DeviceError
import com.interswitchng.interswitchpossdk.shared.interfaces.CardInsertedCallback
import com.interswitchng.interswitchpossdk.shared.interfaces.POSDevice
import com.interswitchng.interswitchpossdk.shared.models.CardDetail
import com.interswitchng.interswitchpossdk.shared.models.PaymentInfo
import com.interswitchng.interswitchpossdk.shared.utilities.DialogUtils
import kotlinx.android.synthetic.main.activity_card.*
import org.koin.android.ext.android.inject
import java.text.NumberFormat

class CardActivity : BaseActivity() {

    private val pos: POSDevice by inject()
    private val cardCallback = CardCallback()

    private val dialog by lazy { DialogUtils.getLoadingDialog(this) }
    private var transactionState = CardTransactionState.ShowInsertCard

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

        showNextScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.detachCallback(cardCallback)
    }

    private fun showNextScreen() {
        transactionState = when(transactionState) {
            CardTransactionState.ShowInsertCard -> {
                insertCardContainer.bringToFront()
                CardTransactionState.ChooseAccountType
            }
            CardTransactionState.ChooseAccountType -> {
                accountOptionsContainer.bringToFront()
                CardTransactionState.EnterPin
            }
            CardTransactionState.EnterPin -> {
                insertPinContainer.bringToFront()
                // TODO rectify this
                CardTransactionState.EnterPin
            }
        }
    }

    private fun showAccountOptions() {
        accountOptionsContainer.bringToFront()
    }


    private fun showLoader(message: String) {
        dialog.setMessage(message)
        dialog.show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    internal inner class CardCallback : CardInsertedCallback {

        override fun onCardDetected() {
            runOnUiThread { showLoader("Reading Card") }
        }

        override fun onCardRead(cardDetail: CardDetail) {
            runOnUiThread {
                showNextScreen()
                val lastFour = cardDetail.pan.substring(cardDetail.pan.length - 4)
                cardNumber.text = "xxxx-xxxx-xxxx-$lastFour"
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
