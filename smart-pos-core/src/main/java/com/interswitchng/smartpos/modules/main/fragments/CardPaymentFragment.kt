package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_layout_card_found.*

class CardPaymentFragment : BaseFragment(TAG) {

    private val cardPaymentFragmentArgs by navArgs<CardPaymentFragmentArgs>()

    override fun getLayoutId(): Int = R.layout.isw_fragment_card_payment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dialog = PaymentTypeDialog (PaymentModel.PaymentType.CARD) {

        }

        change_payment_method.setOnClickListener {
            dialog.show(childFragmentManager, TAG)
        }
    }

    companion object {
        const val TAG = "Card Payment"
    }
}