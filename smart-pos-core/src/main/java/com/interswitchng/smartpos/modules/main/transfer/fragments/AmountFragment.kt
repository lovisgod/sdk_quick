package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class AmountFragment : BaseFragment(TAG) {


    override val layoutId: Int
        get() = R.layout.isw_transfer_fragment_amount

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AmountFragment()
        val TAG = "TRANSFER AMOUNT FRAGMENT"
    }
}