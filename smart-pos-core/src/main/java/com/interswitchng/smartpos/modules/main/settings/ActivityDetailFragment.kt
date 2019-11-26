package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.fragments.AmountFragmentArgs
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.TransactionResponseModel
import com.interswitchng.smartpos.modules.main.models.payment
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import kotlinx.android.synthetic.main.isw_activity_detail.*
import java.util.*

class ActivityDetailFragment : BaseFragment(TAG) {

    private val activityDetailFragmentArgs by navArgs<ActivityDetailFragmentArgs>()
    private val transactionLog by lazy { activityDetailFragmentArgs.TransactionLog }

    override val layoutId: Int
        get() = R.layout.isw_activity_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        handlePrintReceiptClick()
    }

    private fun setUpUI() {
        val paymentType = when (transactionLog.paymentType) {
            PaymentType.PayCode.ordinal -> PaymentType.PayCode
            PaymentType.USSD.ordinal -> PaymentType.USSD
            PaymentType.QR.ordinal -> PaymentType.QR
            else -> PaymentType.Card
        }

        isw_transaction_name.text = paymentType.toString()

        val cardIcon = when (transactionLog.cardType) {
            CardType.MASTER.ordinal -> R.drawable.isw_ic_card_mastercard
            CardType.VISA.ordinal -> R.drawable.isw_ic_card_visa
            CardType.VERVE.ordinal -> R.drawable.isw_ic_card_verve
            else -> R.drawable.isw_ic_payment_card
        }

        isw_transaction_icon.setImageResource(cardIcon)

        isw_amount_paid_value_text.text = transactionLog.amount
        isw_date_value_text.text =  DateUtils.timeOfDateFormat.format(Date(transactionLog.time))
    }

    private fun handlePrintReceiptClick() {
        isw_refund_label.setOnClickListener {
            val payment = payment {
                type = PaymentModel.TransactionType.REFUND
            }
            val direction = ActivityDetailFragmentDirections.iswActionGotoFragmentAmount(payment)
            navigate(direction)
        }

        isw_activity_nav.setOnClickListener {
            navigateUp()
        }
    }

    companion object {
        const val TAG = "ACTIVITY DETAIL FRAGMENT"
    }
}
