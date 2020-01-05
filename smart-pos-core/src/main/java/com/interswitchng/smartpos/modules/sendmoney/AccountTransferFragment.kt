package com.interswitchng.smartpos.modules.sendmoney


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.main.models.PaymentModel
import com.interswitchng.smartpos.modules.main.settings.AccountFragmentDirections
import com.interswitchng.smartpos.shared.activities.BaseFragment
import com.interswitchng.smartpos.shared.utilities.DisplayUtils
import com.interswitchng.smartpos.shared.utilities.isVisible
import kotlinx.android.synthetic.main.fragment_account_transfer.*
import kotlinx.android.synthetic.main.fragment_account_transfer.backImg
import kotlinx.android.synthetic.main.fragment_account_transfer.isw_proceed
import kotlinx.android.synthetic.main.isw_fragment_bill_payment.*


class AccountTransferFragment : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.fragment_account_transfer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {

        backImg.setOnClickListener {
            navigateUp()
        }


        isw_proceed.setOnClickListener {
                proceedToPayment()
        }


    }

    private fun proceedToPayment() {
        val payment = PaymentModel()
        payment.type = PaymentModel.TransactionType.CARD_PURCHASE
        val direction =
            AccountTransferFragmentDirections.iswActionIswAccounttransferfragmentToIswFragmentAmount(payment)
        navigate(direction)
    }

    companion object {

        const val TAG = "ECASH FRAGMENT"
    }

}
