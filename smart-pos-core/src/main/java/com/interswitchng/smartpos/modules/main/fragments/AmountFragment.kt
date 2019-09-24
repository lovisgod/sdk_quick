package com.interswitchng.smartpos.modules.main.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.dialogs.PaymentTypeDialog
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_amount.*

class AmountFragment : BaseFragment(TAG) {

    private val amountFragmentArgs by navArgs<AmountFragmentArgs>()
    private val payment by lazy { amountFragmentArgs.PaymentModel }

    override fun getLayoutId(): Int = R.layout.isw_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isw_proceed.setOnClickListener {
            payment.newPayment {
                amount = isw_amount.text.toString()
            }
            val bottomDialog = PaymentTypeDialog { payment.newPayment {
                paymentType = it
                val direction = AmountFragmentDirections.iswActionGotoFragmentCardPayment(payment)
                navigate(direction)
            } }

            bottomDialog.show(childFragmentManager, TAG)
        }
        isw_amount_toolbar.setNavigationOnClickListener {
            navigateUp()
        }
    }

    companion object {
        const val TAG = "AMOUNT FRAGMENT"
    }
}