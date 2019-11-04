package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.cardModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_card_details.*

class CardDetailsFragment : BaseFragment(TAG) {

    private val cardDetailsFragmentArgs by navArgs<CardDetailsFragmentArgs>()
    private val paymentModel by lazy { cardDetailsFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_card_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_card_details_toolbar.setNavigationOnClickListener { navigateUp() }
        val cardModel = cardModel {
            cvv = isw_cvv.text.toString()
            cardPan = isw_card_pan.text.toString()
            expiryDate = isw_card_expiry_date.text.toString()
        }
        paymentModel.newPayment {
            card = cardModel
            amount = isw_amount.text.toString().toInt()
        }
        isw_proceed.setOnClickListener {
            val direction = CardDetailsFragmentDirections.iswActionGotoFragmentProcessingTransaction(paymentModel)
            navigate(direction)
        }
    }

    companion object {
        const val TAG = "Card Details Fragment"
    }
}