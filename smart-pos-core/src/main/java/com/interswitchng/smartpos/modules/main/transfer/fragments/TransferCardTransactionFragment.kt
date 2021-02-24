package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class TransferCardTransactionFragment : BaseFragment(TAG) {
    override val layoutId: Int
        get()  = R.layout.isw_fragment_transfer_card_transaction


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferCardTransactionFragment()
        val TAG = "TRANSFER CARD TRANSACTION FRAGMENT"
    }
}