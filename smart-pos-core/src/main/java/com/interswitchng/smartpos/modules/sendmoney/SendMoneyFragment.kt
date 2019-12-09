package com.interswitchng.smartpos.modules.sendmoney


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment
import kotlinx.android.synthetic.main.isw_fragment_sendmoney.*
import kotlinx.android.synthetic.main.isw_fragment_sendmoney.backImg
import kotlinx.android.synthetic.main.isw_fragment_transaction.*


class SendMoneyFragment : BaseFragment(TAG) {


    override val layoutId: Int
        get() = R.layout.isw_fragment_sendmoney

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeViews()
    }

    private fun initializeViews() {

        backImg.setOnClickListener {
            navigateUp()
        }

        isw_ecash_card.setOnClickListener {
            val direction = SendMoneyFragmentDirections.iswActionIswSendmoneyfragmentToIswEcashfragment()
            navigate(direction)
        }

        isw_echange_card.setOnClickListener {
            val direction = SendMoneyFragmentDirections.iswActionIswSendmoneyfragmentToIswEchangefragment()
            navigate(direction)
        }

        isw_account_transfer_card.setOnClickListener {
            val direction = SendMoneyFragmentDirections.iswActionIswSendmoneyfragmentToIswAccounttransferfragment()
            navigate(direction)
        }

        isw_arbiter_status_card.setOnClickListener {
            val direction = SendMoneyFragmentDirections.iswActionIswSendmoneyfragmentToIswArbitrationstatusfragment()
            navigate(direction)
        }


    }

    companion object {

        const val TAG = "SEND MONEY FRAGMENT"
    }

}
