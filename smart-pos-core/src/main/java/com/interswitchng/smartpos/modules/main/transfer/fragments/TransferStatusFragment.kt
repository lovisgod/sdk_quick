package com.interswitchng.smartpos.modules.main.transfer.fragments

import android.os.Bundle
import android.view.View
import com.interswitchng.smartpos.R
import com.interswitchng.smartpos.shared.activities.BaseFragment

class TransferStatusFragment() : BaseFragment(TAG) {

    override val layoutId: Int
        get() = R.layout.isw_fragment_transfer_status

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TransferStatusFragment()
        val TAG = "TRANSFER STATUS FRAGMENT"
    }


}