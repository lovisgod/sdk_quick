package com.interswitchng.smartpos.modules.main.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.modules.sendmoney.PinFragmentArgs
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_transaction_sent.*


class TransactionSentFragment : BaseFragment(TAG) {

    private val pinFragmentArgs by navArgs<PinFragmentArgs>()
    private val payment by lazy { pinFragmentArgs.PaymentModel }

    override val layoutId: Int
        get() = R.layout.isw_fragment_transaction_sent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        isw_payment_type.text = payment.type.toString()

        //isw_amount_paid.text = String.format("NGN %s", payment.formattedAmount)
        isw_amount_paid.text = String.format("NGN %s", "10.00") //Added on request from Eddy

        isw_done.setOnClickListener(){
            val direction = TransactionSentFragmentDirections.actionTransactionSentFragmentToIswSendmoneyfragment()
            navigate(direction)
        }
    }

    companion object {

        const val TAG = "TRANSACTION SENT FRAGMENT"
    }
}
