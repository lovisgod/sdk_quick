package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_amount.*

class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (payment.type == PaymentModel.MakePayment.PRE_AUTHORIZATION) {
            isw_proceed.text = getString(R.string.isw_pre_authorize)
        }
        isw_proceed.setOnClickListener {
            payment.newPayment {
                amount = isw_amount.text.toString()
            }
            when (payment.type) {
                PaymentModel.MakePayment.PURCHASE -> {
                    val bottomDialog = PaymentTypeDialog { payment.newPayment {
                        paymentType = it
                        val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                        navigate(direction)
                    } }
                    bottomDialog.show(childFragmentManager, TAG)
                }
                PaymentModel.MakePayment.PRE_AUTHORIZATION -> {
                    val direction = AmountFragmentDirections.iswActionGotoFragmentPin(payment)
                    navigate(direction)
                }
                PaymentModel.MakePayment.COMPLETION -> {
                    val direction = AmountFragmentDirections.iswActionGotoFragmentProcessingTransaction(payment)
                    navigate(direction)
                }
            }
        }
        isw_amount_toolbar.setNavigationOnClickListener {
            navigateUp()
        }
    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }
}