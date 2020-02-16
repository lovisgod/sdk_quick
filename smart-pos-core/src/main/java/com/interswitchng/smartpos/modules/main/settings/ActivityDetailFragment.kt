package com.interswitchng.smartpos.modules.main.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.models.payment
import com.interswitchng.smartpos.shared.Constants.EMPTY_STRING
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.models.printer.info.TransactionType
import com.interswitchng.smartpos.shared.models.transaction.PaymentType
import com.interswitchng.smartpos.shared.models.transaction.cardpaycode.CardType
import com.interswitchng.smartpos.shared.services.iso8583.utils.DateUtils
import kotlinx.android.synthetic.main.isw_activity_detail.*
import java.util.*

class ActivityDetailFragment : BaseFragment(TAG) {

    private val activityDetailFragmentArgs by navArgs<ActivityDetailFragmentArgs>()
    private val transactionLog by lazy { activityDetailFragmentArgs.TransactionLog }
    lateinit var  transactionType: String

    override val layoutId: Int
        get() = R.layout.isw_activity_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        handleMoreClick()
    }

    private fun setUpUI() {
        val paymentType = when (transactionLog.paymentType) {
            PaymentType.PayCode.ordinal -> PaymentType.PayCode
            PaymentType.USSD.ordinal -> PaymentType.USSD
            PaymentType.QR.ordinal -> PaymentType.QR
            else -> PaymentType.Card
        }

        transactionType = when(transactionLog.transactionType) {
            TransactionType.Purchase.ordinal -> getString(R.string.isw_purchase)
            TransactionType.Pre_Authorization.ordinal -> getString(R.string.isw_pre_authorization)
            TransactionType.Completion.ordinal -> getString(R.string.isw_completion)
            TransactionType.Refund.ordinal -> getString(R.string.isw_refund)
            TransactionType.Reversal.ordinal -> getString(R.string.isw_reversal)
            else -> EMPTY_STRING
        }

        val cardIcon = when (transactionLog.cardType) {
            CardType.MASTER.ordinal -> R.drawable.isw_ic_card_mastercard
            CardType.VISA.ordinal -> R.drawable.isw_ic_card_visa
            CardType.VERVE.ordinal -> R.drawable.isw_ic_card_verve
            else -> R.drawable.isw_ic_payment_card
        }

        isw_transaction_name.text = paymentType.toString()
        isw_transaction_icon.setImageResource(cardIcon)
        isw_transaction_type.text = getString(R.string.isw_transaction_type, transactionType)
        isw_amount_paid_value_text.text = getString(R.string.isw_amount_with_naira_sign, transactionLog.amount)
        isw_date_value_text.text =  DateUtils.timeOfDateFormat.format(Date(transactionLog.time))
    }

    private fun handleMoreClick() {
        isw_more_label.setOnClickListener {
            val payment = payment {
                type = when(transactionType) {
                    "Purchase" -> PaymentModel.TransactionType.CARD_PURCHASE
                    "Pre-Authorization" -> PaymentModel.TransactionType.PRE_AUTHORIZATION
                    "Completion" -> PaymentModel.TransactionType.COMPLETION
                    "Refund" -> PaymentModel.TransactionType.REFUND
                    "Reversal" -> PaymentModel.TransactionType.REVERSAL
                    else -> PaymentModel.TransactionType.CARD_PURCHASE
                }
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
