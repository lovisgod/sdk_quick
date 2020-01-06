package com.interswitchng.smartpos.modules.sendmoney


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_e_cash.*

class eCashFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_e_cash

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        backImg.setOnClickListener {
            navigateUp()
        }

        isw_proceed.setOnClickListener {
            if (formIsValid()) {
                val payment = PaymentModel()
                payment.type = PaymentModel.TransactionType.ECASH
                val direction = eCashFragmentDirections.iswActionIswEcashfragmentToIswFragmentAmount(payment)
                navigate(direction)
            }
        }

    }

    private fun formIsValid(): Boolean {
        if (TextUtils.isEmpty(isw_reference_phonenumber.text)) {
            Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }


    companion object {

        const val TAG = "ECASH FRAGMENT"
    }

}
